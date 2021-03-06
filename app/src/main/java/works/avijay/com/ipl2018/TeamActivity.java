package works.avijay.com.ipl2018;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

import works.avijay.com.ipl2018.helper.BackendHelper;
import works.avijay.com.ipl2018.helper.DatabaseHelper;
import works.avijay.com.ipl2018.helper.players_list_adapter;

public class TeamActivity extends AppCompatActivity {

    String image_url, rate, team_name, original_name;
    int win, loss, remaining, points;
    ListView playersList;
    ImageView team_image;
    Context context;
    DatabaseHelper helper;
    List<players_list_adapter> playersAdapter = new ArrayList<>();
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        getDetails();

        initializeViews();
        showAd();
    }

    private void showAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    public void getDetails(){
        team_image = findViewById(R.id.team_image);
        mAdView = findViewById(R.id.adView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        image_url = extras.getString("team_image");
        rate = extras.getString("rate");
        original_name = extras.getString("team_name");
        setTeamNameAndImage(original_name);
        win = extras.getInt("wins");
        loss = extras.getInt("loss");
        remaining = extras.getInt("remaining");
        points = extras.getInt("points");
    }

    public void setTeamNameAndImage(String name){
        team_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        switch (name){
            case "KINGS XI PUNJAB":
                        team_name = "KXIP";
                        team_image.setImageDrawable(getResources().getDrawable(R.drawable.card_punjab));
                break;

            case "CHENNAI SUPER KINGS":
                        team_name = "CSK";
                        team_image.setImageDrawable(getResources().getDrawable(R.drawable.card_chennai));
                break;

            case "KOLKATA KNIGHT RIDERS":
                        team_name = "KKR";
                        team_image.setImageDrawable(getResources().getDrawable(R.drawable.card_kolkata));
                break;

            case "MUMBAI INDIANS":
                        team_name = "MI";
                        team_image.setImageDrawable(getResources().getDrawable(R.drawable.card_mumbai));
                break;

            case "DELHI DAREDEVILS":
                        team_name = "DD";
                        team_image.setImageDrawable(getResources().getDrawable(R.drawable.card_delhi));
                break;

            case "ROYAL CHALLENGERS BANGALORE":
                        team_name = "RCB";
                        team_image.setImageDrawable(getResources().getDrawable(R.drawable.card_bengaluru));
                break;

            case "RAJASTHAN ROYALS":
                        team_name = "RR";
                        team_image.setImageDrawable(getResources().getDrawable(R.drawable.card_rajastan));
                break;

            case "SUNRISERS HYDERABAD":
                        team_name = "SRH";
                        team_image.setImageDrawable(getResources().getDrawable(R.drawable.card_hyderabad));
                break;
        }
    }

    public void initializeViews(){
        context = getApplicationContext();
        playersList = findViewById(R.id.playersList);
        helper = new DatabaseHelper(context);
        playersAdapter.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = helper.getTeamMembers(team_name);
                while (cursor.moveToNext()){
                    playersAdapter.add(new players_list_adapter(
                            cursor.getString(0), Integer.parseInt(cursor.getString(1)),
                            cursor.getInt(2)>0,
                            cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6)

                    ));
                }
                cursor.close();
            }
        }).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayList();
            }
        }, 200);
    }


    public void displayList(){
        ArrayAdapter<players_list_adapter> adapter = new myPlayersAdapterClass();
        playersList.setAdapter(adapter);
    }


    public class myPlayersAdapterClass extends ArrayAdapter<players_list_adapter> {

        myPlayersAdapterClass() {
            super(context, R.layout.team_member_item, playersAdapter);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(TeamActivity.this);
                itemView = inflater.inflate(R.layout.team_member_item, parent, false);
            }
            players_list_adapter current = playersAdapter.get(position);

            ImageView member_image = itemView.findViewById(R.id.member_image);
            TextView capped = itemView.findViewById(R.id.capped);
            ImageView overseas = itemView.findViewById(R.id.overseas);
            ImageView player_type = itemView.findViewById(R.id.player_type);
            TextView member_name = itemView.findViewById(R.id.member_name);
            TextView country = itemView.findViewById(R.id.country);
            TextView member_bidding_price = itemView.findViewById(R.id.member_bidding_price);


            long price = current.getPlayer_bidding_price();
            member_bidding_price.setText((price/10000000.0)+"Cr");




            if(!current.isPlayer_capped()){
                capped.setText("Uncapped");
            }else {
                capped.setText("");
            }

            if(!current.getPlayer_country().equals("IND")){
                overseas.setImageResource(R.drawable.overseas);
                country.setText(current.getPlayer_country());
            }else {
                overseas.setImageResource(0);
                country.setText("IND");
            }

            switch (current.getPlayer_type()) {
                case "Bowl":
                    player_type.setImageResource(R.drawable.ball_icon);
                    break;
                case "AR":
                    player_type.setImageResource(R.drawable.bat_ball_icon);
                    break;
                case "Bat":
                    player_type.setImageResource(R.drawable.bat_icon);
                    break;
                case "WK":
                    player_type.setImageResource(R.drawable.keeper_icon);
                    break;
            }

            member_name.setText(current.getPlayername());

            Glide.with(context).load(current.getPlayer_image())
                    .thumbnail(0.5f)
                    .centerCrop()
                    .placeholder(R.drawable.general_player)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(member_image);

            return itemView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
