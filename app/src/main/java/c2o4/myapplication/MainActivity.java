package c2o4.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity {

    SearchView search;
    boolean auto = true;
    private double latitude = 6.927079;
    private double longitude = 79.861244;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Geocoder coder = new Geocoder(this);

        //region Recherche
        search = (SearchView) findViewById(R.id.searchView);
        //Au moment de la recherche
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //SET Latitude and Longitude manually
                List<Address> address;

                try {
                    address = coder.getFromLocationName(query,5);
                    if(address.size()!=0){
                        auto = false;
                        Address location=address.get(0);
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();
                    }
                    else{
                        auto = true;
                        alerte("Pas de résultat");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                hideSoftKeyboard();

                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //A la sortie de la recherche
        search.setOnCloseListener(new SearchView.OnCloseListener(){

            @Override
            public boolean onClose() {
                auto = true;
                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
                //SET Lat and Long back to GPS
                return false;
            }
        });
        //endregion

    }

    @Override
    protected void onResume(){
        super.onResume();

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }


    //region ViewPager

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private static final int NUM_PAGES =3;

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return ScreenSlidePageFragment.newInstance(0,latitude,longitude);
                case 1: return ScreenSlidePageFragment.newInstance(1,latitude,longitude);
                case 2: return ScreenSlidePageFragment.newInstance(2,latitude,longitude);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    //endregion

    public void hideSoftKeyboard(){
        if(getCurrentFocus()!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    private void alerte (String alerttext){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alerte");
        alertDialog.setMessage(alerttext);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}