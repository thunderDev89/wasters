package com.acemen.android.wasters.fragment;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.acemen.android.wasters.Adresse;
import com.acemen.android.wasters.Common;
import com.acemen.android.wasters.R;
import com.acemen.android.wasters.WasteType;
import com.acemen.android.wasters.restful.SendWastesRequest;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.acemen.android.wasters.R.id.send_button;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContributeFragment extends Fragment {
    private static String LOG_TAG = ContributeFragment.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Button mCaptureButton;
    private Button mSendButton;
    private ImageView mCaptureView;
    private TextView mCoordsTv;
    private Spinner mWasteTypesLb;

    private String mCurrentCapturePath;
    private Adresse mAdresse;

    public ContributeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contribute_fragment, container, false);


        mCaptureView = (ImageView) rootView.findViewById(R.id.capture_view);
        mCoordsTv = (TextView) rootView.findViewById(R.id.coords_textview);

        mWasteTypesLb = (Spinner) rootView.findViewById(R.id.waste_type_listbox);
        ArrayAdapter<WasteType> adapter = new ArrayAdapter<WasteType>(getActivity(), R.layout.support_simple_spinner_dropdown_item, WasteType.values());
        mWasteTypesLb.setAdapter(adapter);

        mCaptureButton = (Button) rootView.findViewById(R.id.photo_button);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mSendButton = (Button) rootView.findViewById(send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInfos();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode ==  getActivity().RESULT_OK) {
            Picasso.with(getActivity()).load(Uri.parse(mCurrentCapturePath)).resize(800, 600).into(mCaptureView);
//            mCaptureView.setImageURI(Uri.parse(mCurrentCapturePath));
            manageCoordinates();
        }
    }

    private void manageCoordinates() {
        Location location = Common.getCurrentLocation(getActivity());
        if (location != null) {
            Address address = geocode(location.getLatitude(), location.getLongitude());
            if (address != null) {
                mAdresse = new Adresse(address.getSubThoroughfare(),
                        address.getThoroughfare(),
                        address.getPostalCode(),
                        address.getLocality(),
                        address.getLongitude(),
                        address.getLatitude());

                mCoordsTv.setText(mAdresse.toString());
            }
        }
    }

    private Address geocode(double lat, double lon) {
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        Address address = null;
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                address = addresses.get(0);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void sendInfos() {
        SendWastesRequest request = new SendWastesRequest.Builder()
                .addContext(getActivity())
                .addFilePath(mCurrentCapturePath)
                .addWasteType(((WasteType) mWasteTypesLb.getSelectedItem()).getType())
                .addAdresse(mAdresse)
                .build();
        request.execute();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES); //getExternalFilesDir specify private dir for this app only
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentCapturePath = "file:"+image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the file where the photo should go
            File captureFile = null;
            try {
                captureFile = createImageFile();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error when creating capture file", e);
            }
            // Continue if file is created
            if (captureFile != null) {
                Uri captureUri = FileProvider.getUriForFile(getActivity(),
                        "com.wasters.android.fileprovider",
                        captureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
