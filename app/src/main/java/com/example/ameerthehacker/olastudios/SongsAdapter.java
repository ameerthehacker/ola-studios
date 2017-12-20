package com.example.ameerthehacker.olastudios;

/**
 * Created by ameerthehacker on 20/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Song> songList;
    private History history;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            history = new History(mContext);
        }
    }


    public SongsAdapter(Context mContext, List<Song> songList) {
        this.mContext = mContext;
        this.songList = songList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Song song = songList.get(position);
        holder.title.setText(song.getName());
        holder.count.setText(song.getArtistsString());

        // loading album cover using Glide library
        Glide.with(mContext).load(song.getCoverImage()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_song, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        int position;

        public MyMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_download:
                    Song song = songList.get(position);
                    String downloadUrl = song.getUrl();
                    new DownloadHandler(song).execute(downloadUrl);
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
    // Class for downloading music
    class DownloadHandler extends AsyncTask<String, String, String> {

        private Song song;

        DownloadHandler(Song song) {
            this.song = song;
        }
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(mContext, "Downloading the song....", Toast.LENGTH_LONG).show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            String TAG = "debug";
            try{
                Log.v(TAG, "downloading data");

                URL url  = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                Log.v(TAG, "lenghtOfFile = "+lenghtOfFile);

                InputStream is = url.openStream();

                FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/" + song.getName() + ".mp3");

                byte data[] = new byte[1024];

                int count = 0;
                long total = 0;
                int progress = 0;

                while ((count=is.read(data)) != -1)
                {
                    total += count;
                    int progress_temp = (int)total*100/lenghtOfFile;
                    if(progress_temp%10 == 0 && progress != progress_temp){
                        progress = progress_temp;
                        Log.v(TAG, "total = "+progress);
                    }
                    fos.write(data, 0, count);
                }

                is.close();
                fos.close();

            }catch(Exception e){
                Log.v(TAG, "exception in downloadData");
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {

        }

        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // Say that the song was downloaed
            Toast.makeText(mContext, "Download complete", Toast.LENGTH_LONG).show();
            // Store in the history table
            history.insert("Downloaded " + song.getName());
        }
    }

}