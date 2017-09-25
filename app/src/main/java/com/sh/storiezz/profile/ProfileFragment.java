package com.sh.storiezz.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh.storiezz.R;
import com.sh.utils.Const;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String USER_ID = "user_id";
    private String uid;


    private FirebaseAuth mAuth;
    private CircleImageView mUserImage;
    private TextView mUserName,mUserBio;
    private View mMainView;
    private DatabaseReference mUserRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String uid) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_profile, container, false);

        mUserImage = (CircleImageView) mMainView.findViewById(R.id.profile_user_image);
        mUserName = (TextView) mMainView.findViewById(R.id.profile_username);
        mUserBio = (TextView) mMainView.findViewById(R.id.profile_bio);

        mUserRef = FirebaseDatabase.getInstance().getReference().child(Const.DB_REF_USERS).child(uid);
        mUserRef.keepSynced(true);


        mAuth = FirebaseAuth.getInstance();

        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG,dataSnapshot + " " + uid);

                String name = dataSnapshot.child(Const.DB_REF_USER_NAME_KEY).getValue().toString();
                mUserName.setText(name);
                final String image  = dataSnapshot.child(Const.DB_REF_USER_IMAGE_KEY).getValue().toString();
                if(dataSnapshot.child(Const.DB_REF_USER_BIO_KEY).getValue() != null){
                    String bio = dataSnapshot.child(Const.DB_REF_USER_BIO_KEY).getValue().toString();
                    mUserBio.setText(bio);
                }
                Picasso.with(ProfileFragment.this.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_pic).into(mUserImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                        Picasso.with(ProfileFragment.this.getContext()).load(image).placeholder(R.drawable.default_pic).into(mUserImage);
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
