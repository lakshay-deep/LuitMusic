package com.vianet.musicplayer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vianet.musicplayer.adapters.CustomAdapter;
import com.vianet.musicplayer.dataloader.JsonParse1;
import com.vianet.musicplayer.service.SongService;
import com.vianet.musicplayer.util.UtilFunctions;


public class AudioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public RecyclerView mRVFish;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static AudioActivity audioActivity;
    static int offline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        audioActivity = this;
        Intent i = getIntent();
        offline = i.getIntExtra("offline", 1);
//        Toast.makeText(getBaseContext(),"dfhjsdbsdhsgdgs",Toast.LENGTH_SHORT).show();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mRVFish = (RecyclerView) findViewById(R.id.fishPriceList);
        mRVFish.setVisibility(View.INVISIBLE);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (isNetworkAvailable()) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            fragmentTransaction.add(R.id.content_main, SongsList.newInstance(3, 0)).addToBackStack(null);
            fragmentTransaction.commit();
        }
//        mViewPager.setPageTransformer(true, new RotateUpTransformer());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    public void onBackPressed() {
        if (isNetworkAvailable() && offline == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(audioActivity, R.style.AlertDialogCustom);
            builder.setTitle("Luit")
                    .setMessage("Network available, want to go online?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Go Online", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getBaseContext(), LandingPage.class));
                            finish();
                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (isNetworkAvailable()) {
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


//            MenuItem showPlayer = menu.findItem(R.id.musicPlayer);

        return true;

        // Get Search item fro.m action bar and Get Search service
//        MenuItem searchItem = menu.findItem(R.id.search);
//        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView();
//        }
//        if (searchView != null) {
//
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
//            searchView.setIconifiedByDefault(false);
//        }
//        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//            @Override
//            public void onViewAttachedToWindow(View view) {
////                mRVFish.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onViewDetachedFromWindow(View view) {
//                mRVFish.setVisibility(View.INVISIBLE);
//            }
//
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.musicPlayer) {
            if (UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext())) {
                startActivity(new Intent(audioActivity, AudioPlayerActivity.class));
            } else {
                Toast.makeText(audioActivity, "Music is not playing.", Toast.LENGTH_SHORT).show();
            }
        }
//        if (id == R.id.search){
//            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//            SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResults.class)));
//            searchView.setIconifiedByDefault(false);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera && offline != 0) {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.nav_gallery && offline != 0) {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_slideshow && offline != 0) {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.nav_share) {
            shareApp(AudioActivity.this);
        } else if (id == R.id.nav_send) {
            launchMarket();
        } else if (id == R.id.nav_video && offline != 0) {
            if (UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext())) {
                Intent i = new Intent(getBaseContext(), SongService.class);
                stopService(i);
            }
            finish();
            startActivity(new Intent(getBaseContext(), LandingPage.class));
        } else if (id == R.id.nav_offline) {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            fragmentTransaction.add(R.id.content_main, SongsList.newInstance(3, 0)).addToBackStack(null);
            fragmentTransaction.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main3, container, false);
            GridView listView = (GridView) rootView.findViewById(R.id.listView);

            final int tabPosition = getArguments().getInt(ARG_SECTION_NUMBER);
            CustomAdapter customAdapter;
            AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            switch (tabPosition) {
                case 0:
                    customAdapter = new CustomAdapter(getContext(), LandingPage.album.size(), tabPosition);
                    listView.setAdapter(customAdapter);
                    break;
                case 1:
                    customAdapter = new CustomAdapter(getContext(), LandingPage.artist.size(), tabPosition);
                    listView.setAdapter(customAdapter);
                    break;
                case 2:
                    customAdapter = new CustomAdapter(getContext(), JsonParse1.thumbnail.length, tabPosition);
                    listView.setAdapter(customAdapter);
                    break;
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    SongsList songsList = new SongsList(tabPosition, i);
//                    songsList.newInstance(tabPosition, i);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                    fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                    fragmentTransaction.add(R.id.content_main, SongsList.newInstance(tabPosition, i)).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
//            Toast.makeText(getContext(), ""+tabPosition, Toast.LENGTH_SHORT).show();
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

//    @Override
//    protected void onResume() {
//        super.onResume();
////        Toast.makeText(getBaseContext(), "On Resume", Toast.LENGTH_SHORT).show();
//        try{
//            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
//            if (isServiceRunning) {
//                updateUI();
//            }else{
//                linearLayoutPlayingSong.setVisibility(View.GONE);
//            }
//            changeButton();
//            new SongsList().seekBarProgress();
//        }catch(Exception e){}
//    }

    public static void shareApp(Context context) {

//        int applicationNameId = context.getApplicationInfo().labelRes;
        String applicationName = "LUIT";
        final String appPackageName = context.getPackageName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
//        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(applicationName));
        String text =  "Download this at:";
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Intent.EXTRA_TEXT,applicationName + "\n" + text + " " + link);
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
        try {
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
            if (isServiceRunning) {
                Intent i = new Intent(getBaseContext(), SongService.class);
                stopService(i);
            } else {
                finish();
            }
            if (AudioPlayerActivity.context != null) {
                AudioPlayerActivity.context.finish();
            }
        } catch (Exception e) {
        }
    }
}




