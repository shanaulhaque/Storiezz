package com.sh.storiezz;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh.utils.Const;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity  {


    private final static String  TAG = SettingActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private CircleImageView mUserImage;
    private TextInputLayout mUserName;
    private TextInputLayout mUserBio;
    private Button mSaveButton;

    private DatabaseReference mUserDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mToolbar = (Toolbar) findViewById(R.id.setting_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mUserImage = (CircleImageView) findViewById(R.id.setting_user_image);
        mUserName = (TextInputLayout) findViewById(R.id.setting_username);
        mUserBio = (TextInputLayout) findViewById(R.id.setting_bio);
        mSaveButton = (Button) findViewById(R.id._setting_save_changes);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Fetching Details");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Const.DB_REF_USERS).child(mUser.getUid());
        mUserDatabaseReference.keepSynced(true);

        mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressDialog.dismiss();
                String name = dataSnapshot.child(Const.DB_REF_USER_NAME_KEY).getValue().toString();
                mUserName.getEditText().setText(name);
                final String image  = dataSnapshot.child(Const.DB_REF_USER_IMAGE_KEY).getValue().toString();
                if(dataSnapshot.child(Const.DB_REF_USER_BIO_KEY).getValue() != null){
                    String bio = dataSnapshot.child(Const.DB_REF_USER_BIO_KEY).getValue().toString();
                    mUserBio.getEditText().setText(bio);
                }
                Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_pic).into(mUserImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                        Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.default_pic).into(mUserImage);
                    }
                });





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
                Log.e(TAG,databaseError.toString());
                Toast.makeText(SettingActivity.this,"Unable read data",Toast.LENGTH_SHORT);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setTitle("Updating Status");
                mProgressDialog.setMessage("Please wait...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                String bio = mUserBio.getEditText().getText().toString();
                String name = mUserName.getEditText().getText().toString();
                if(TextUtils.isEmpty(name) == false){

                    Map request = new HashMap();
                    request.put(Const.DB_REF_USER_NAME_KEY,name);
                    request.put(Const.DB_REF_USER_BIO_KEY,bio);
                    mUserDatabaseReference.updateChildren(request, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null) {
                                mProgressDialog.dismiss();
                                Toast.makeText(SettingActivity.this, "Detail Saved", Toast.LENGTH_SHORT);

                            }
                        }
                    });
                }
            }
        });

    }
}
