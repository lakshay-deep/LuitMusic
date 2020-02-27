package com.vianet.musicplayer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vianet.musicplayer.adapters.CustomAdapterVideo;
import com.vianet.musicplayer.dataloader.JsonParseVideo;
import com.vianet.musicplayer.service.SongService;
import com.vianet.musicplayer.util.UtilFunctions;

public class VideoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public RecyclerView mRVFish;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Toast.makeText(getBaseContext(),"dfhjsdbsdhsgdgs",Toast.LENGTH_SHORT).show();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mRVFish = findViewById(R.id.fishPriceList);
        mRVFish.setVisibility(View.INVISIBLE);
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        mViewPager.setPageTransformer(true, new RotateUpTransformer());

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
//    public static boolean flag = false;
    @Override
    public void onBackPressed() {

        if (isNetworkAvailable()) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(i);
                startActivity(new Intent(getBaseContext(), LandingPage.class));
                finish();
            }
        } else {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(i);
        }
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0){
////            super.onBackPressed();
//            getSupportFragmentManager().popBackStack();
////            flag = false;
//        }else {
//            Intent i = new Intent(Intent.ACTION_MAIN);
//            i.addCategory(Intent.CATEGORY_HOME);
//            i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
//            startActivity(i);
//            startActivity(new Intent(getBaseContext(), LandingPage.class));
//            finish();
//        }


    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.nav_gallery) {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_slideshow) {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.nav_share) {
            shareApp(VideoActivity.this);
        } else if (id == R.id.nav_send) {
            launchMarket();
        }
        if (id == R.id.nav_audio){
            finish();
            startActivity(new Intent(getBaseContext(), LandingPage.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_video, container, false);
            GridView listView = rootView.findViewById(R.id.listView);
            AdView mAdView = rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            final int tabPosition = getArguments().getInt(ARG_SECTION_NUMBER);
            CustomAdapterVideo customAdapter;
            switch (tabPosition){
                case 0:
                    customAdapter = new CustomAdapterVideo(getContext(), LandingPage.album.size(), tabPosition);
                    listView.setAdapter(customAdapter);
                    break;
                case 1:
                    customAdapter = new CustomAdapterVideo(getContext(), LandingPage.artist.size(), tabPosition);
                    listView.setAdapter(customAdapter);
                    break;
                case 2:
                    customAdapter = new CustomAdapterVideo(getContext(), JsonParseVideo.ytId.length, tabPosition);
                    listView.setAdapter(customAdapter);
                    break;

            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (tabPosition != 2) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.content_main_video, VideosList.newInstance(tabPosition, i)).addToBackStack(null);
                        fragmentTransaction.commit();
                    }else {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.content_main_video, VideoDetails.newInstance(tabPosition, 0, i)).addToBackStack(null);
                        fragmentTransaction.commit();

//                        Intent intent = new Intent(getContext(), YoutubePlayer.class);
//                        intent.putExtra("clickPosition", i);
//                        intent.putExtra("tabPosition", tabPosition);
//                        startActivity(intent);
                    }
//                    Toast.makeText(getContext(), ""+i, Toast.LENGTH_SHORT).show();
                }
            });

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;//ParseJSONSongTab.jsonArray.length();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Album";
                case 1:
                    return "Artist";
                case 2:
                    return "Songs";
            }
            return null;
        }
    }


    public static void shareApp(Context context)
    {

        int applicationNameId = context.getApplicationInfo().labelRes;
        final String appPackageName = context.getPackageName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(applicationNameId));
        String text = R.string.share_app_text+"\nDownload this at:";
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
        context.startActivity(Intent.createChooser(i, "Share link:"));
    }

    public void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Unable to store rating...Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
            if (isServiceRunning) {
                Intent i = new Intent(getBaseContext(), SongService.class);
                stopService(i);
            }else{
                finish();
            }
        }catch(Exception ignored){}
    }
}