//package com.tehilafi.ama;
//
//import android.annotation.SuppressLint;
//import android.app.Application;
//import android.net.Uri;
//import android.util.Log;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.media2.exoplayer.external.ExoPlayerFactory;
//import androidx.media2.exoplayer.external.source.ExtractorMediaSource;
//import androidx.media2.exoplayer.external.upstream.DefaultHttpDataSourceFactory;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
//import com.google.android.exoplayer2.extractor.ExtractorsFactory;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//import com.google.android.exoplayer2.trackselection.TrackSelector;
//import com.google.android.exoplayer2.ui.PlayerView;
//import com.google.android.exoplayer2.upstream.BandwidthMeter;
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
//
//public class ViewHolder extends RecyclerView.ViewHolder {
//    static View mView;
//    private static SimpleExoPlayer exoPlayer;
//    private static PlayerView mExoplayerView;
//    private Object ExtractorsFactory;
//
//    public ViewHolder(@NonNull View itemView){
//        super(itemView);
//        mView = itemView;
//    }
//
//    @SuppressLint("RestrictedApi")
//    public static void setVideo(final Application ctx, final String uri){
//        mExoplayerView = mView.findViewById(R.id.exoID);
//
//        try{
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(ctx).build();
//            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
//            exoPlayer = (SimpleExoPlayer) ExtractorsFactory.newSimpleInstance(ctx);
//            Uri video = Uri.parse(uri);
//            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
//            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
//            mExoplayerView.setPlayer(exoPlayer);
//            exoPlayer.prepare(mediaSource);
//            exoPlayer.setPlayWhenReady(false);
//        }
//        catch (Exception e){
//            Log.e("ViewHolder", "exoplayer error" + e.toString());
//        }
//
//    }
//}
