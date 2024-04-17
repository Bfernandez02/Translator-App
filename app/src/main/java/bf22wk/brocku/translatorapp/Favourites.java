package bf22wk.brocku.translatorapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Favourites extends AppCompatActivity implements RecyclerViewInterface {

    RecyclerView favouritesLV;
    DataHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourites);
        MainActivity.recents = false;
        MainActivity.selectedTranslation = null;

        db = new DataHelper(this);
        MainActivity.recentList = db.FetchRecent();

        favouritesLV = findViewById(R.id.favouritesLV);
        TranslationListAdapter translationListAdapter = new TranslationListAdapter(
                getApplicationContext(), MainActivity.favouriteList,
                this);

        favouritesLV.setAdapter(translationListAdapter);
        favouritesLV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goBack(View view) {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClickFavourite(int position) {
        TranslationListAdapter translationListAdapter = new TranslationListAdapter(
                getApplicationContext(), MainActivity.favouriteList,
                this);

        favouritesLV.setAdapter(translationListAdapter);
        favouritesLV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void handleSettingsClick(View view) {
        Intent intent = new Intent();
        setResult(3, intent);
        finish();
    }

    public void handleRecentClick(View view) {
        Intent intent = new Intent();
        setResult(2, intent);
        finish();
    }

    public void handleFavouritesClick(View view) {
    }
}