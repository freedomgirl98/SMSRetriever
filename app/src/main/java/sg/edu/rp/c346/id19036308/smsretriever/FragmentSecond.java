package sg.edu.rp.c346.id19036308.smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSecond#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSecond extends Fragment {
    EditText etWord;
    TextView tvSMS;
    Button btnRetrieve;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentSecond() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSecond.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSecond newInstance(String param1, String param2) {
        FragmentSecond fragment = new FragmentSecond();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        etWord = view.findViewById(R.id.etGetWord);
        tvSMS = view.findViewById(R.id.tvDisplay);
        btnRetrieve = view.findViewById(R.id.btnWord);

        String editWord = etWord.getText().toString();

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                    //stops the action from proceeding further as permission is not granted yet
                    return;
                }
                //create all message URI
                Uri uri = Uri.parse("content://sms");
                //The columns we want
                //date is when the message took place
                //address is the number of the other party
                //body is the message content
                //type 1 is received, type 2 is sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                //Get Content Resolver object from which to
                //query the content provider
                ContentResolver cr = getActivity().getContentResolver();



                //The filter
                String filter = "body LIKE ?";
                // The matches for the ?
                String[] filterArgs = {"%"+ etWord.getText().toString() +"%"};

                //Fetch SMS Message from Build-in Content provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null, null);
                String smsBody = "";
                if (cursor.moveToFirst()){
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyy h:mm;ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")){
                            type = "inbox: ";
                        }
                        else {
                            type = "sent: ";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSMS.setText(smsBody);

            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnRetrieve.performClick();
                }
                else{
                    Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}