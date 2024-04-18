package bf22wk.brocku.translatorapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Recents extends AppCompatActivity implements RecyclerViewInterface {

    RecyclerView recentLV;

    DataHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recents);
        recentLV = findViewById(R.id.recentLV);
        MainActivity.selectedTranslation = null;
        MainActivity.recents = true;
        db = new DataHelper(this);

        // Fetches updated list of recent translations
        MainActivity.recentList = db.FetchRecent();

        // Populates recycler view based on the list of recents
        TranslationListAdapter translationListAdapter = new TranslationListAdapter(
                getApplicationContext(), MainActivity.recentList,
                this);

        recentLV.setAdapter(translationListAdapter);
        recentLV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Back to the main class
    public void goBack(View view) {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    // Handles when an item is clicked
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    // Handles when user favourites a translation
    @Override
    public void onItemClickFavourite(int position) {
        TranslationListAdapter translationListAdapter = new TranslationListAdapter(
                getApplicationContext(), MainActivity.recentList,
                this);

        recentLV.setAdapter(translationListAdapter);
        recentLV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void handleSettingsClick(View view) {
        Intent intent = new Intent();
        setResult(3, intent);
        finish();
    }

    public void handleRecentClick(View view) {
    }

    public void handleFavouritesClick(View view) {
        Intent intent = new Intent();
        setResult(1, intent);
        finish();
    }
}