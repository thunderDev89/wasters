import { createServer, Server } from 'http';
import * as express from 'express';
import * as socketIo from 'socket.io';
import { QuizBackend } from "./backend/quiz-backend";

import { Message } from './model';
import { BackendCache } from './backend/backend-cache';
import { UserResponse } from './model/quiz/response.model';
import { Question } from './model/quiz/question.model';

export class ChatServer {
    public static readonly PORT: number = 8080;
    private static readonly TEASING_DURATION: number = 5000; // 5 secondes
    private app: express.Application;
    private server: Server;
    private io: SocketIO.Server;
    private port: string | number;

    constructor() {
        this.createApp();
        this.config();
        this.createServer();
        this.sockets();
        this.listen();
    }

    private createApp(): void {
        this.app = express();
    }

    private createServer(): void {
        this.server = createServer(this.app);
    }

    private config(): void {
        this.port = process.env.PORT || ChatServer.PORT;
    }

    private sockets(): void {
        this.io = socketIo(this.server);
    }

    private listen(): void {
        this.server.listen(this.port, () => {
            console.log('Running server on port %s', this.port);
        });

        this.io.on('connect', (socket: any) => {
            console.log('Connected client on port %s.', this.port);

            socket.on('message', (m: Message) => {
                console.log('[server](message): %s', JSON.stringify(m));
                this.io.emit('message', m);
            });

            let isAdmin: boolean = false;
            let username: string;
            let room: number = 1;
            let backend: QuizBackend;
            let correctAnswer =
                socket.on('user_connect', (r: any) => {
                    console.log("user_connect => %s", JSON.stringify(r));
                    isAdmin = r.role === 'admin';
                    username = r.from;
                    socket.emit('user_connected');
                });

            socket.on('connect_room', (r: any) => {
                room = r.quizId;
                console.log('connect_rom: %s =>', JSON.stringify(r));
                backend = BackendCache.getBackend(r.quizId);
                socket.join(backend.quizId);
            });

            socket.on('check_data', _ => {
                console.log('username = %s,, room = %d,, isAdmin = %s', username, room, isAdmin);

            });

            // ===== ADMIN INPUT EVENTS =====
            socket.on('launch_quiz', _ => {
                if (isAdmin) {
                    const question = backend.getNextQuestion();
                    socket.to(room).broadcast.emit('teasing_question', question.title);
                    this.teaseQuestion()
                    .then((o: boolean) => this.sendAnswers(socket, room, question))
                    .then((o: boolean) => this.haltQuestion(socket, room, question.duration));

                    // socket.to(room).broadcast.emit('teasing_question', question.title);
                    // setTimeout(() => {
                    //     socket.to(room).broadcast.emit('select_answers', question);
                    //     setTimeout(() => {
                    //         socket.to(room).emit('halt_question', question);
                    //     }, question.duration * 1000);
                    // }, ChatServer.TEASING_DURATION);
                }
            });

            socket.on('disconnect', () => {
                console.log('Client disconnected');
            });
        });
    }

    private sendAnswers(socket: any, room: number, question: Question): Promise<boolean> {
        socket.to(room).broadcast.emit('select_answers', question);
        return this.displayQuestion(question.duration);
    }

    private haltQuestion(socket: any, room:number, duration: number) {
        socket.to(room).emit('halt_question', 'stop');
    }

    private teaseQuestion(): Promise<boolean> {
        const promise: Promise<boolean> = new Promise((resolve, reject) => {
            setTimeout(() => {
                resolve(true);
            }, ChatServer.TEASING_DURATION);
        });
        return promise;
    }

    private displayQuestion(duration: number): Promise<boolean> {
        const promise: Promise<any> = new Promise((resolve, reject) => {
            setTimeout(() => {
                resolve(true);
            }, duration);
        });
        return promise;
    }

    public getApp(): express.Application {
        return this.app;
    }
}
