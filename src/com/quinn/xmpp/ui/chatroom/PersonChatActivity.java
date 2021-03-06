package com.quinn.xmpp.ui.chatroom;

import java.io.File;
import java.util.ArrayList;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.atermenji.android.iconicdroid.icon.FontAwesomeIcon;
import com.atermenji.android.iconicdroid.icon.IconicIcon;
import com.quinn.xmpp.Intents;
import com.quinn.xmpp.Intents.Builder;
import com.quinn.xmpp.R;
import com.quinn.xmpp.RequestCodes;
import com.quinn.xmpp.core.chatroom.TextMessageListener;
import com.quinn.xmpp.ui.BaseActivity;
import com.quinn.xmpp.ui.BaseDataItem;
import com.quinn.xmpp.ui.chatroom.FileDataItem.FileState;
import com.quinn.xmpp.ui.contacts.ContactsDataItem;
import com.quinn.xmpp.util.DisplayUtils;
import com.quinn.xmpp.util.FileUtils;
import com.quinn.xmpp.util.ImageFormatUtils;
import com.quinn.xmpp.util.LogcatUtils;
import com.quinn.xmpp.util.TimeUtils;
import com.quinn.xmpp.util.ToastUtils;

public class PersonChatActivity extends BaseActivity implements
		OnRefreshListener {

	@InjectView(R.id.toolbar)
	Toolbar toolbar;
	@InjectView(R.id.userchat_recycle_view)
	RecyclerView mRecyclerView;
	@InjectView(R.id.personchat_swipe_layout)
	SwipeRefreshLayout swipeRefreshLayout;
	@InjectView(R.id.chatMsgInput)
	EditText input;
	@InjectView(R.id.chatMsgTextSend)
	Button send;

	@InjectView(R.id.menu_float)
	FloatingActionsMenu menu_float;
	@InjectView(R.id.btn_file)
	FloatingActionButton btn_file;
	@InjectView(R.id.btn_photo)
	FloatingActionButton btn_photo;
	@InjectView(R.id.btn_location)
	FloatingActionButton btn_location;

	private String jidChattingWithWho;
	private String nicknameChattingWithWho;
	private String serviceChattingWithWho;
	private RecyclerView.LayoutManager mLayoutManager;
	private TextMessageListener textMessageListener;
	private ArrayList<BaseDataItem> dataItems;
	private PersonChatAdapter adapter;
	private Chat chat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_chat);
		ButterKnife.inject(this);
		jidChattingWithWho = getStringExtra(Intents.EXTRA_JID_CHATTING_WITH_WHO);
		nicknameChattingWithWho = getStringExtra(Intents.EXTRA_NICKNAME_CHATTING_WITH_WHO);
		serviceChattingWithWho = getStringExtra(Intents.EXTRA_SERVICE_CHATTING_WITH_WHO);
		LogcatUtils.v("Come in to a person-chatroom");
		LogcatUtils.v("jidChattingWithWho = " + jidChattingWithWho);
		LogcatUtils.v("serviceChattingWithWho = " + serviceChattingWithWho);

		toolbar.setTitle(nicknameChattingWithWho);
		setSupportActionBar(toolbar);
		// 以下三行代码使activity有向上返回的按钮
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		dataItems = new ArrayList<BaseDataItem>();
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		swipeRefreshLayout.setOnRefreshListener(this);
		adapter = new PersonChatAdapter(this, dataItems);
		mRecyclerView.setAdapter(adapter);
		input.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					menu_float.collapse();
			}
		});

		initChatManager();
		/**
		 * init float btn
		 */
		btn_file.setIconDrawable(ImageFormatUtils.buildIconFont(this,
				FontAwesomeIcon.FILE, Color.WHITE));
		btn_photo.setIconDrawable(ImageFormatUtils.buildIconFont(this,
				FontAwesomeIcon.PICTURE, Color.WHITE));
		btn_location.setIconDrawable(ImageFormatUtils.buildIconFont(this,
				IconicIcon.LOCATION, Color.WHITE));

	}

	/**
	 * 
	 */
	public void initChatManager() {
		ChatManager chatManager = smack.getConnection().getChatManager();
		textMessageListener = new TextMessageListener();

		// 收到消息后，要发送到handler里更新，否则需要打开输入法或者关闭输入法，才会刷新
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				adapter.notifyDataSetChanged();
			}
		};
		//chat.removeMessageListener(arg0)
		// 这里需要full id 用addPacketListener可以获得
		
		chat = chatManager.createChat(jidChattingWithWho + "/"
				+ serviceChattingWithWho, new MessageListener() {

			@Override
			public void processMessage(Chat chat, Message message) {
				LogcatUtils.v("receive msg = " + message.getBody());
				// message.get
				PersonChatDataItem dataItem = new PersonChatDataItem();
				dataItem.setTextContent(message.getBody());
				dataItem.setJid(jidChattingWithWho);
				dataItem.setNickname(smack.getContactData(jidChattingWithWho)
						.getNickname());
				dataItem.setHappenTime(TimeUtils.getCurrentTime2String());
				dataItem.setItemType(BaseDataItem.LEFT_BUBBLE_TEXT);
				dataItems.add(dataItem);
				handler.sendEmptyMessage(1);
			}
		});
	}

	@Override
	public void onRefresh() {
		swipeRefreshLayout.setRefreshing(false);
	}

	/**
	 * Load message before s certain time :date
	 * 
	 * @param date
	 */
	public void loadOlderChattingMessage(String date) {

	}

	@OnClick(R.id.chatMsgTextSend)
	public void onSendText() {
		try {
			chat.sendMessage(input.getText().toString());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		PersonChatDataItem dataItem = new PersonChatDataItem();
		dataItem.setHappenTime(TimeUtils.getCurrentTime2String());
		dataItem.setTextContent(input.getText().toString());
		dataItem.setJid(smack.getUserVCard().getJid());
		dataItem.setItemType(BaseDataItem.RIGHT_BUBBLE_TEXT);
		dataItems.add(dataItem);
		adapter.notifyDataSetChanged();
		input.setText("");
		DisplayUtils.closeInputMethod(this);
	}

	@OnClick(R.id.btn_photo)
	public void onSendPhoto() {
		Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);  
		openAlbumIntent.setType("image/*");  
		startActivityForResult(openAlbumIntent, RequestCodes.CHOOSE_PHOTO_REQUEST);  
	}

	@OnClick(R.id.btn_file)
	public void onSendFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					RequestCodes.CHOOSE_FILE_REQUEST);
		} catch (android.content.ActivityNotFoundException ex) {
			ToastUtils.showMsg(this, "Please install a File Manager.");
		}

	}

	@OnClick(R.id.btn_location)
	public void onSendLocation() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.person_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case RequestCodes.CHOOSE_FILE_REQUEST:
				Uri uri = data.getData();
				String path = FileUtils.getPathFromUri(this, uri);
				File file = new File(path);
				LogcatUtils.i("you choose file = " + path);
				String filename = file.getName();
				String filesize = FileUtils.getFileSizeString(file);
				FileDataItem fileDataItem = new FileDataItem(filename, filesize, FileState.REQUEST_STATE);
				fileDataItem.setItemType(BaseDataItem.RIGHT_BUBBLE_FILE);
				dataItems.add(fileDataItem);
				adapter.notifyDataSetChanged();
				smack.sendFile(file, jidChattingWithWho + "/"
						+ serviceChattingWithWho);
			break;
		case RequestCodes.CHOOSE_PHOTO_REQUEST:
			
			break;
		}
	}

	public static Intent createIntent(ContactsDataItem dataitem) {
		Builder builder = new Builder("PersonChat.View")
				.add(Intents.EXTRA_JID_CHATTING_WITH_WHO, dataitem.getJid())
				.add(Intents.EXTRA_NICKNAME_CHATTING_WITH_WHO,
						dataitem.getNickname())
				.add(Intents.EXTRA_SERVICE_CHATTING_WITH_WHO,
						dataitem.getService());
		return builder.toIntent();
	}

}
