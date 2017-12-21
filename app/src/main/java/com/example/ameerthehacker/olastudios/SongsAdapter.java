package com.example.ameerthehacker.olastudios;

/**
 * Created by ameerthehacker on 20/12/17.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private String songUrl;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private ExtractorsFactory extractorsFactory;
    private TrackSelection.Factory trackSelectionFactory;
    private TrackSelector trackSelector;
    private DefaultBandwidthMeter defaultBandwidthMeter;
    private DataSource.Factory dataSourceFactory;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;
        public ImageView control;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            control = (ImageView) view.findViewById(R.id.btnControl);
            history = new History(mContext);

        }
    }


    public SongsAdapter(Context mContext, List<Song> songList) {
        this.mContext = mContext;
        this.songList = songList;

        bandwidthMeter = new DefaultBandwidthMeter();
        extractorsFactory = new DefaultExtractorsFactory();

        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        defaultBandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "mediaPlayerSample"), defaultBandwidthMeter);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Song song = songList.get(position);
        holder.title.setText(song.getName());
        holder.count.setText(song.getArtistsString());
        final boolean isPlaying = false;

        // loading album cover using Glide library
        Glide.with(mContext).load(song.getCoverImage()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
        // Play or pause the song
        songUrl = song.getUrl();
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            private boolean isPlaying = false;
            private MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(songUrl), dataSourceFactory, extractorsFactory, null, null);

            private SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);

            @Override
            public void onClick(View view) {


                if(!isPlaying) {
                    player.prepare(mediaSource);
                    player.setPlayWhenReady(true);
                    history.insert("Played song " + song.getName());

                }
                else {
                    player.setPlayWhenReady(false);
                }

                player.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if(playWhenReady && playbackState == ExoPlayer.STATE_READY) {
                            holder.control.setImageResource(R.drawable.pause);
                            isPlaying = true;
                        }
                        if(!playWhenReady) {
                            holder.control.setImageResource(R.drawable.play);
                            isPlaying = false;
                        }
                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        Toast.makeText(mContext, "Unable to play the song check your internet connection", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {

                    }
                });
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
        private ProgressDialog progressDialog;

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
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Downloading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setCancelable(true);
            progressDialog.setProgress(0);
            progressDialog.show();
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



                InputStream is = url.openStream();

                FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/" + song.getName() + ".mp3");

                byte data[] = new byte[1024];

                int count = 0;
                long total = 0;
                int progress = 0;

                while ((count=is.read(data)) != -1)
                {
                    total += count;
                    progress = (int)total*100/lenghtOfFile;
                    fos.write(data, 0, count);
                    publishProgress(Integer.toString(progress));
                }

                is.close();
                fos.close();

            }catch(Exception e){
                progressDialog.dismiss();
                Toast.makeText(mContext, "Unable to download song check your internet connection", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // Say that the song was downloaed
            progressDialog.dismiss();
            // Store in the history table
            history.insert("Downloaded song " + song.getName());
        }
    }

}