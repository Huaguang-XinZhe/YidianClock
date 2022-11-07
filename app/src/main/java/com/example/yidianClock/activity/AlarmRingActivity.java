package com.example.yidianClock.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.MyHashMap;
import com.example.yidianClock.MyUtils;
import com.example.yidianClock.adapter.RingAdapter;
import com.example.yidianClock.databinding.ActivityAlarmRingBinding;
import com.example.yidianClock.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmRingActivity extends AppCompatActivity {
    ActivityAlarmRingBinding ringBinding;
    int checkedPositionBefore = -1;
    //当前选中的item所对应的ringtone
    Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ringBinding = ActivityAlarmRingBinding.inflate(getLayoutInflater());
        setContentView(ringBinding.getRoot());

        RecyclerView recyclerView = ringBinding.ringRv;

        //数据获取，构建List
        String upTitle = this.getIntent().getStringExtra("title");
        List<Song> songsList = getSongsList(upTitle);
        Map<Integer, Ringtone> ringtoneMap = new MyHashMap<>();
        //返回上个activity的intent
        Intent intent = new Intent();
        //选中位置列表
        List<Integer> checkedPositionList = new ArrayList<>();

        //从sp中取出当前铃声的position，无铃声返回的是-1
        SharedPreferences sp = this.getSharedPreferences("sp", MODE_PRIVATE);
        String currentRingFrom = sp.getString("currentRingFrom", "");
        int positionCR = sp.getInt("currentRingPosition", -1);
        boolean isAllowSelected = positionCR != -1 && currentRingFrom.equals(upTitle);
        //铃声复选（只在最初显示的时候选中，其他时候不选中）
        if (isAllowSelected) {
            songsList.get(positionCR).setSelected(true);
            //默认选中的设为上次选中position，并加入选中列表
            checkedPositionBefore = positionCR;
            checkedPositionList.add(positionCR);
        }


        //创建并设置adapter
        RingAdapter adapter = new RingAdapter(this, songsList);
        recyclerView.setAdapter(adapter);

        //创建并设置layoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // TODO: 2022/11/7  将复选后的铃声移动到屏幕内
        if (isAllowSelected) {
            //移到顶部
//            recyclerView.scrollToPosition(positionCR);
            //移到距离顶部还有offset的距离
            layoutManager.scrollToPositionWithOffset(positionCR, 500);
        }

        //recyclerView中各个item的监听事件（在视图绑定显示的时候就会加载，但触发才会执行）
        adapter.setOnListener((holder, position) -> {//—————————————————————————————————————————————————————adapter

            holder.itemView.setOnClickListener(v -> {
                //将点击的item数据设为选中，上次点击的item数据设为未选中，并进行局部更新__________________________
                songsList.get(position).setSelected(true);
                adapter.notifyItemChanged(position);
                //积累选中位置到list中
                checkedPositionList.add(position);
                int length = checkedPositionList.size();
                //第二次选中才会执行
                if (length > 1) {
                    checkedPositionBefore = checkedPositionList.get(length - 2);
                    //上次点击的和本次点击的不是同一个item才会执行
                    if (checkedPositionBefore != position) {
                        songsList.get(checkedPositionBefore).setSelected(false);
                        //使adapter在指定item进行局部刷新（不管同不同屏都刷新）
                        adapter.notifyItemChanged(checkedPositionBefore);
//                        //同一屏幕显示出来的item的position范围（本次点击）
//                        int firstPosition = layoutManager.findFirstVisibleItemPosition();
//                        int lastPosition = layoutManager.findLastVisibleItemPosition();
//                        //上次点击和本次点击在同屏position范围之内才会执行
//                        if (checkedPositionBefore >= firstPosition && checkedPositionBefore <= lastPosition) {
//                            //使adapter在指定item进行局部刷新
//                            adapter.notifyItemChanged(checkedPositionBefore);
//                        }
                    }
                }

                //将选中数据传回上个activity_____________________________________________________________
                Song selectedSong = songsList.get(position);
                intent.putExtra("title", selectedSong.getSongName());
                //这个方法得到的uri不正确，id有问题
//                intent.putExtra("songsUriStr", selectedSong.getSongsUri().toString());
                intent.putExtra("songsUriStr", selectedSong.getSongsUri() + "");
                intent.putExtra("positionMapStr", upTitle + "_" + position);
                //这两个方法得到的歌名是一致的
//                  Log.i("getSongsList", "ringtone.getTitle = " + ringtone.getTitle(this) +
//                    "，selectedSong.getSongName" + selectedSong.getSongName());
                //通过Activity的setResult方法传回所需数据
                this.setResult(RESULT_OK, intent);

                //播放、停止___________________________________________________________________________
                Ringtone mRingtone = RingtoneManager.getRingtone(this, selectedSong.getSongsUri());
                ringtoneMap.put(position, mRingtone);
                //只要position相同，就会返回最开始的那个Ringtone对象
                ringtone = ringtoneMap.get(position);
                assert ringtone != null;
                //使用闹钟渠道控制
//                MyUtils.setAlarmControl(this, ringtone);
//                //第一次没有checkedPositionBefore值，故直接播放
//                ringtone.play();
                Log.i("getSongsList", "播放啦");
                //checkedPositionBefore不为初始值，即选中过item
                Log.i("getSongsList", "checkedPositionBefore = " + checkedPositionBefore);
                if (checkedPositionBefore != -1) {
                    if (position != checkedPositionBefore) {
                        Log.i("getSongsList", "No ! item");
                        Ringtone ringtoneBefore = ringtoneMap.get(checkedPositionBefore);
                        if (ringtoneBefore != null) {
                            if (ringtoneBefore.isPlaying()) {
                                ringtoneBefore.stop();
                                ringtone.play();
                            } else {
                                ringtone.play();
                            }
                        }
                    } else {
                        Log.i("getSongsList", "点击了同一个item");
                        if (ringtone.isPlaying()) {
                            Log.i("getSongsList", "正在播放");
                            ringtone.stop();
                        } else {
                            Log.i("getSongsList", "不在播放");
                            ringtone.play();
                        }
                    }
                } else {
                    //没选中过item（第一次点击选中，自动选中的不行）
                    ringtone.play();
                }

                //把这段代码放在这都不会播放了！！！
//                //在播放停止逻辑结束后应用闹钟渠道调节音量
//                MyUtils.setAlarmControl(this, ringtone);

            });

        });//———————————————————————————————————————from adapter——————————————————————————————————————————————————

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //什么都没点就返回了
        if (ringtone != null) {
            ringtone.stop();
        }
    }

    /**
     * 从手机获取本地歌曲
     * @return 歌曲列表
     */
    private List<Song> getSongsList(String upTitle) {
        List<Song> songsList = new ArrayList<>();
        Cursor cursor;

        if (upTitle.equals("本地铃声")) {
            ContentResolver resolver = this.getContentResolver();
            //MediaStore.Images.Media.EXTERNAL_CONTENT_URI，这是获取图片的uri，还是有区别的
            cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, "artist!=?", new String[]{"<unknown>"}, null);
            while (cursor.moveToNext()) {
                //这得到的是title列名的索引号（37），artist得到的是9
                int i = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                String title = cursor.getString(i);
                i = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                String artist = cursor.getString(i);
                i = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//            //data = /storage/emulated/0/qqmusic/song/杨沛宜 - 歌唱祖国 [mqms2].mp3
                String data = cursor.getString(i);
                //dataUri = content://media/external_primary/audio/media（固定的）
//            //以这个uri播放，所有的音乐都只会播放同一个铃声——钢琴声，这个uri能正常播放
                Uri dataUri = MediaStore.Audio.Media.getContentUriForPath(data);
                //nameUri = content://media/internal/audio/media（固定的）
                //这个uri的播放也和上面那个一样，钢琴声，这个uri不能正常播放
//            Uri nameUri = MediaStore.Audio.Media.getContentUriForPath(title);
                //id = 1474
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //只能使用id，使用title作为pathSegment会抛出异常
                Uri songsUri = Uri.withAppendedPath(dataUri, "" + id);

                songsList.add(new Song(title, artist, songsUri));
            }
        } else {
            //系统铃声
            RingtoneManager manager = new RingtoneManager(this);
            cursor = manager.getCursor();
            //访问系统铃声时，厂商似乎是做了限制，无论是使用for循环还是使用cursor都会延迟一段时间然后再打开
            while (cursor.moveToNext()) {
                int position = cursor.getPosition();
                String title = manager.getRingtone(position).getTitle(this);
                Uri songsUri = manager.getRingtoneUri(position);
                songsList.add(new Song(title, "", songsUri));
            }
        }
        cursor.close();

        return songsList;
    }


}