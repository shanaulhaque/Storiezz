package com.sh.storiezz.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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


public class FollowersFragment extends Fragment {



    private static final String TAG = FollowersFragment.class.getSimpleName();

    private FirebaseAuth mAuth;
    private DatabaseReference mFollowerDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    private RecyclerView mFollowerList;

    private String mCurrentUserId;
    private View mMainView;

    public FollowersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_followers, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mFollowerDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Const.DB_REF_FOLLOWERS).child(mCurrentUserId);
        mFollowerDatabaseReference.keepSynced(true);
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Const.DB_REF_USERS);
        mUserDatabaseReference.keepSynced(true);
        mFollowerList = (RecyclerView) mMainView.findViewById(R.id.fragment_follower_list);
        mFollowerList.setHasFixedSize(true);
        mFollowerList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Followers,FollowerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Followers,FollowersFragment.FollowerViewHolder>(
                Followers.class,
                R.layout.users_single_layout,
                FollowersFragment.FollowerViewHolder.class,
                mFollowerDatabaseReference
        ) {

            @Override
            protected void populateViewHolder(final FollowersFragment.FollowerViewHolder viewHolder, final Followers model, int position) {
                final String listUserId = getRef(position).getKey();
                Log.i(TAG,listUserId);
                mUserDatabaseReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child(Const.DB_REF_USER_NAME_KEY).getValue().toString();

                        final String image = dataSnapshot.child(Const.DB_REF_USER_IMAGE_KEY).getValue().toString();

                        viewHolder.setName(name);
                        viewHolder.setImage(image);
                        if(dataSnapshot.hasChild(Const.DB_REF_USER_BIO_KEY)){
                            String bio = dataSnapshot.child(Const.DB_REF_USER_BIO_KEY).getValue().toString();
                            viewHolder.setBio(bio);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mFollowerList.setAdapter(firebaseRecyclerAdapter);

    }

    private static class FollowerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FollowerViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name) {
            TextView mUsername = (TextView) mView.findViewById(R.id.list_user_name);
            mUsername.setText(name);
        }

        public void setBio(String bio) {
            TextView mBio = (TextView) mView.findViewById(R.id.list_user_bio);
            mBio.setText(bio);
        }




        public void setImage(final String image) {
            final CircleImageView imageView = (CircleImageView) mView.findViewById(R.id.list_user_image);

            Picasso.with(mView.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_pic).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mView.getContext()).load(image).placeholder(R.drawable.default_pic).into(imageView);
                }
            });

        }
    }

}
