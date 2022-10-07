package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer, MainPresenter.MainView {
    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast infoToast;
    private User selectedUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;

    private MainPresenter presenter = new MainPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
            statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
        });

        updateSelectedUserFollowingAndFollowers();

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        Picasso.get().load(selectedUser.getImageUrl()).into(userImageView);

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        //Presenter?
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            presenter.initiateIsFollower(selectedUser);
        }

        followButton.setOnClickListener(v -> {
            followButton.setEnabled(false);

            if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {
                presenter.initiateUnfollow(selectedUser);
            } else {
                presenter.initiateFollow(selectedUser);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            presenter.initiateLogout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStatusPosted(String post) { presenter.initiatePostStatus(post); }

    public void updateSelectedUserFollowingAndFollowers() { presenter.initiateGetCounts(selectedUser); }

    public void updateFollowButton(boolean following) {
        if (following) {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        } else {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public void logoutUser() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void displayMessage(String message) {
        infoToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        infoToast.show();
    }

    @Override
    public void clearMessage() {
        if (infoToast != null) {
            infoToast.cancel();
            infoToast = null;
        }
    }

    @Override
    public void updateFolloweeCount(int count) { followeeCount.setText(getString(R.string.followeeCount, String.valueOf(count))); }

    @Override
    public void updateFollowerCount(int count) { followerCount.setText(getString(R.string.followerCount, String.valueOf(count))); }

    @Override
    public void enableFollowButton() { followButton.setEnabled(true); }

    @Override
    public void updateCounts() { updateSelectedUserFollowingAndFollowers(); }

    @Override
    public void updateFollowButtonState(boolean state) { updateFollowButton(state); }

    @Override
    public String getFollowMessage() { return "Adding " + selectedUser.getName() + "..."; }

    @Override
    public String getUnfollowMessage() { return "Removing " + selectedUser.getName() + "..."; }

}
