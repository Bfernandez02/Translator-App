package bf22wk.brocku.translatorapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    DataHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        db = new DataHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void clearAllTranslations(View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning! All translations will be deleted")
                .setCancelable(true)
                .setPositiveButton("Continue", (dialog, which) -> {
                    db.ClearAll();
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel()).show();
    }

    public void clearAllRecents(View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning! All recent translations will be deleted")
                .setCancelable(true)
                .setPositiveButton("Continue", (dialog, which) -> {
                    db.removeAllRecents();
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel()).show();
    }

    public void clearAllFavourites(View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning! All favourite translations will be deleted")
                .setCancelable(true)
                .setPositiveButton("Continue", (dialog, which) -> {
                    db.removeAllFavourites();
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel()).show();
    }
}