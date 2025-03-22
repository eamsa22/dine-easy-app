package org.m2sdl.dineeasy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;
import org.m2sdl.dineeasy.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected void setupToolbar() {
        ImageView menuIcon = findViewById(R.id.menu_icon);
        if (menuIcon != null) {
            menuIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v);
                }
            });
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.Main){
                    startActivity(new Intent(BaseActivity.this, MainActivity.class));
                    return true;
                }

                else if ( item.getItemId() == R.id.Consultation){
                    startActivity(new Intent(BaseActivity.this, ConsultationActivity.class));
                    return true;
                }
                else if( item.getItemId() == R.id.home){
                    startActivity(new Intent(BaseActivity.this, HomeActivity.class));
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }
}
