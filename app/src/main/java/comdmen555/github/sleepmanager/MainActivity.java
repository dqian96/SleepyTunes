package comdmen555.github.sleepmanager;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //caching ads
        AdBuddiz.setPublisherKey("1e51939d-0f6c-480a-8db5-9afe1ab796b4");
        AdBuddiz.cacheAds(this); // this = current Activity


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as a parent activity in AndroidManifest.xml is specified
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Button Consequences

    //Open the music timer activity
    public void openMusicTimerActivity(View view) {
        Intent intent = new Intent(this, MusicTimerActivity.class);
        startActivity(intent);
    }

    //opens app info activity
    public void openAppInfoActivity(View view) {
        Intent intent = new Intent(this, AppInfoActivity.class);
        startActivity(intent);
    }

    //alert dialog that instructs the user how to use the app
    public void howToUseDialog(View view) {
        AlertDialog.Builder howToUseMessageDialogBuilder = new AlertDialog.Builder(this);
        howToUseMessageDialogBuilder.setMessage(R.string.howToUseString);
        howToUseMessageDialogBuilder.setPositiveButton("Got it!",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });


        AlertDialog howToUseMessageDialog = howToUseMessageDialogBuilder.create();
        howToUseMessageDialog.setTitle("How to use this app");
        howToUseMessageDialog.show();
    }

}
