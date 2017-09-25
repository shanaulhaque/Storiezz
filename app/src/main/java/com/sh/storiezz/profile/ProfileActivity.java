package com.sh.storiezz.profile;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh.storiezz.R;
import com.sh.storiezz.SettingActivity;
import com.sh.utils.Const;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private final static String TAG = ProfileActivity.class.getSimpleName();


    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private int counterNumber[] = new int[]{0,0,0};

    private DatabaseReference mFollowerReference;
    private DatabaseReference mFollowingReference;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String userId = getIntent().getStringExtra(Const.USER_ID);


        mToolbar = (Toolbar) findViewById(R.id.profile_page_toolbar);
        mViewPager = (ViewPager) findViewById(R.id.profile_pager);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFollowerReference = FirebaseDatabase.getInstance().getReference().child(Const.DB_REF_FOLLOWERS).child(userId);
        mFollowingReference = FirebaseDatabase.getInstance().getReference().child(Const.DB_REF_FOLLOWING).child(userId);




        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.profile_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        ProfileFragment profileFragment = ProfileFragment.newInstance(userId);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_frame, profileFragment).commit();


        mFollowerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    counterNumber[0] = (int) dataSnapshot.getChildrenCount();
                    mSectionsPagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFollowingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    counterNumber[1] = (int) dataSnapshot.getChildrenCount();
                    mSectionsPagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }


    public String getCount(int position) {

        return "" + counterNumber[position];
    }
}
