package bf22wk.brocku.translatorapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TranslationListAdapter extends RecyclerView.Adapter<TranslationListAdapter.MyViewHolder> {

    public final List<translate> translations;
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    static DataHelper db;

    public TranslationListAdapter(Context context, List<translate> translations,
                        RecyclerViewInterface recyclerViewInterface)
    {
        db = new DataHelper(context);
        this.translations = translations;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;

    }

    @NonNull
    @Override
    public TranslationListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.translation_cell, parent, false);
        return new TranslationListAdapter.MyViewHolder(view, recyclerViewInterface, translations, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        final translate translation = translations.get(position);

        holder.fromLanguageText.setText(translation.getFrom_language());
        holder.toLanguageText.setText(translation.getTo_language());
        holder.originText.setText(translation.getText());
        holder.translatedText.setText(translation.getTranslated_text());

        if (MainActivity.recents){
            holder.starView.setImageResource(R.drawable.hollowstar);
        }
    }

    @Override
    public int getItemCount()
    {
        return translations.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView starView; TextView fromLanguageText, toLanguageText, originText, translatedText;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface,
                            List<translate> translations, Context context) {
            super(itemView);
            fromLanguageText = itemView.findViewById(R.id.fromLanguageText);
            toLanguageText = itemView.findViewById(R.id.toLanguageText);
            originText = itemView.findViewById(R.id.originText);
            translatedText = itemView.findViewById(R.id.translatedText);
            starView = itemView.findViewById(R.id.translationCellStar);

            itemView.setOnClickListener(v -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();


                    if (position != RecyclerView.NO_POSITION){
                        MainActivity.selectedTranslation = translations.get(position);
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            });


            starView.setOnClickListener(v -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();


                    if (position != RecyclerView.NO_POSITION) {

                        if (MainActivity.recents) {
                            translate selected = translations.get(position);
                            MainActivity.selectedTranslation = selected;
                            MainActivity.favouriteList.add(selected);

                            db.RemoveRecent(selected);
                            db.insertFAVOURITE(MainActivity.selectedTranslation.getFrom_language(),
                                    MainActivity.selectedTranslation.getTo_language(),
                                    MainActivity.selectedTranslation.getText(),
                                    MainActivity.selectedTranslation.getTranslated_text());
                            translations.remove(position);
                            Toast.makeText(context, "Translation added to Favourites!", Toast.LENGTH_SHORT).show();
                            recyclerViewInterface.onItemClickFavourite(position);
                        }else{
                            translate selected = translations.get(position);
                            db.RemoveFavourite(selected);
                            translations.remove(position);
                            Toast.makeText(context, "Translation removed from Favourites!", Toast.LENGTH_SHORT).show();
                            recyclerViewInterface.onItemClickFavourite(position);
                        }

                    }
                }
            });
        }
    }
}
