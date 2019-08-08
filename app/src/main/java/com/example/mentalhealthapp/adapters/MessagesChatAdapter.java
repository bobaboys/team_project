package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.DownloadTaskAndPlay;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.SendBird;
import com.sendbird.android.Sender;
import com.sendbird.android.UserMessage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import Utils.Utils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MessagesChatAdapter extends RecyclerView.Adapter<MessagesChatAdapter.ViewHolder> {
    private Context context;
    private List<BaseMessage> messages;
    private boolean isMyMessage, isTextMessage;
    private ParseUser addressee;

    public MessagesChatAdapter(Context context, List<BaseMessage> messages) {
        this.context = context;
        this.messages = messages;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_types_message, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        BaseMessage message = messages.get(i);
        try{
            isTextMessage= true;
            createViewText( viewHolder,  message);

        }catch (ClassCastException e){
            isTextMessage = false;
            createViewAudio(viewHolder,message);
        }
        viewHolder.bind();

    }


    private void createViewText(ViewHolder viewHolder, BaseMessage message){
        viewHolder.userMessage  = (UserMessage) message;
        Sender sender = viewHolder.userMessage.getSender();
        isMyMessage = isThisMyMessage(sender);
        viewHolder.isMyMessage=isMyMessage;
        viewHolder.myAudioLayout.setVisibility(LinearLayout.GONE);
        viewHolder.yourAudioLayout.setVisibility(LinearLayout.GONE);
        if(isMyMessage){
            viewHolder.myMessageLayout.setVisibility(LinearLayout.VISIBLE);
            viewHolder.yourMessageLayout.setVisibility(LinearLayout.GONE);
        }else{
            viewHolder.myMessageLayout.setVisibility(LinearLayout.GONE);
            viewHolder.yourMessageLayout.setVisibility(LinearLayout.VISIBLE);
        }
    }


    private void createViewAudio(ViewHolder viewHolder, BaseMessage message){
        viewHolder.yourMessageLayout.setVisibility(LinearLayout.GONE);
        viewHolder.myMessageLayout.setVisibility(LinearLayout.GONE);

        viewHolder.fileMessage = (FileMessage) message;
        Sender sender = viewHolder.fileMessage.getSender();
        isMyMessage = isThisMyMessage(sender);
        viewHolder.isMyMessage=isMyMessage;
        if(isMyMessage){
            viewHolder.myAudioLayout.setVisibility(LinearLayout.VISIBLE);
            viewHolder.yourAudioLayout.setVisibility(LinearLayout.GONE);
        }else{
            viewHolder.myAudioLayout.setVisibility(LinearLayout.GONE);
            viewHolder.yourAudioLayout.setVisibility(LinearLayout.VISIBLE);
        }
    }

    public boolean isThisMyMessage(Sender sender) {
        String senderId = sender.getUserId();
        String currentUserId = SendBird.getCurrentUser().getUserId();
        return  senderId.equals(currentUserId);
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }


    public void setAddressee(ParseUser addressee) {
        this.addressee = addressee;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        private  TextView body, name, myBody;
        private  ImageView profileSender, playYou, playMine;
        private  SeekBar sbYour, sbMine;
        private LinearLayout myMessageLayout, yourMessageLayout, yourAudioLayout, myAudioLayout;
        private FileMessage fileMessage;
        private UserMessage userMessage;
        private boolean isMyMessage;
        private MediaPlayer   player = null;


        View.OnClickListener audioListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrDownload();
            }
        };

        private Handler mSeekbarUpdateHandler = new Handler();

        private Runnable mUpdateSeekbar = new Runnable() {
            @Override
            public void run() {
                if(isMyMessage)
                    sbMine.setProgress(player.getCurrentPosition());
                else
                    sbYour.setProgress(player.getCurrentPosition());
                mSeekbarUpdateHandler.postDelayed(this, 50);
            }
        };


        SeekBar.OnSeekBarChangeListener seekBarChangeListener= new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    player.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };

        private void playOrDownload() {

            try {
                String audioId = fileMessage.getSender().getUserId().toLowerCase();
                audioId += fileMessage.getMessageId()+".3gp";
                String pathDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() ;
                String absFilePath =pathDownload+audioId;
                File outputFile = new File(absFilePath);
                player = new MediaPlayer();

                if(outputFile.exists()){
                        play(absFilePath);
                }else{
                    new DownloadTaskAndPlay( context,  isMyMessage?playMine:playYou,
                            isMyMessage?sbMine:sbYour, fileMessage.getUrl(),pathDownload,
                            audioId,  player);
                }

            } catch (IOException e) {
                Log.e(Constants.AUDIO_RECORD_FAIL_TAG, "prepare() failed");
            }
        }


        MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Utils.enableDisablePlay(context,isMyMessage?playMine:playYou, true);
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                if(isMyMessage)
                    sbMine.setProgress(0);
                else
                    sbYour.setProgress(0);
            }
        };

        public void play(String absFilePath) throws IOException{
            player.setDataSource(absFilePath);
            player.prepare();
            if(isMyMessage) {
                sbMine.setMax(player.getDuration());
                sbMine.setOnSeekBarChangeListener(seekBarChangeListener);
            }
            else {
                sbYour.setMax(player.getDuration());
                sbYour.setOnSeekBarChangeListener(seekBarChangeListener);
            }
            player.start();
            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
            player.setOnCompletionListener(completionListener);

        }


        public ViewHolder(View view) {
            super(view);
            myMessageLayout = view.findViewById(R.id.ly_my_msg);
            yourMessageLayout = view.findViewById(R.id.ly_sender_msg);
            yourAudioLayout = view.findViewById(R.id.ly_your_audio);
            myAudioLayout = view.findViewById(R.id.ly_my_audio);

            name = view.findViewById(R.id.tv_author_message);
            profileSender = view.findViewById(R.id.iv_profile_pic_message);
            myBody = view.findViewById(R.id.my_message_body);
            body = view.findViewById(R.id.message_body);

            playYou = view.findViewById(R.id.iv_play_your_audio);
            playMine = view.findViewById(R.id.iv_play_my_audio);
            sbYour = view.findViewById(R.id.sb_your_audio);
            sbMine = view.findViewById(R.id.sb_my_audio);

            playMine.setOnClickListener(audioListener);
            playYou.setOnClickListener(audioListener);
        }


        public void bind(){
            if(isTextMessage) bindTextMsg();
        }


        private void bindTextMsg(){
            if(isMyMessage)
                myBody.setText(userMessage.getMessage());
            else{
                body.setText(userMessage.getMessage());
                if(addressee==null)return;
                name.setText(addressee.getUsername());
                try {
                    ParseFile avatarPic = addressee.getParseFile("avatar");
                    Glide.with(context)
                            .load(avatarPic.getFile())
                            .bitmapTransform(new RoundedCornersTransformation(context, 120, 0))
                            .into(profileSender);
                }catch (ParseException e){
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
