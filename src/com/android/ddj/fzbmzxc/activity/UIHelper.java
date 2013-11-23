package com.android.ddj.fzbmzxc.activity;

import java.io.IOException;

import com.android.ddj.fzbmzxc.bean.AccessInfo;
import com.android.ddj.fzbmzxc.config.AppConfig;

import com.android.ddj.fzbmzxc.R;
import com.android.ddj.fzbmzxc.common.SinaWeiboHelper;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class UIHelper {
	
	/**
	 * 分享到'新浪微博'或'腾讯微博'的对话框
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的标题
	 * @param url
	 *            分享的链接
	 */
	public static void showShareDialog(final Activity context,
			final String title, final String url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(context.getString(R.string.share));
		builder.setItems(R.array.app_share_items,
				new DialogInterface.OnClickListener() {
			
				AppConfig cfgHelper = AppConfig.getAppConfig(context);
				AccessInfo access = cfgHelper.getAccessInfo();

					public void onClick(DialogInterface arg0, int arg1) {
						switch (arg1) {
						case 0:// 新浪微博
								// 分享的内容
							final String shareMessage = title + " " + url;
							// 初始化微博
							if (SinaWeiboHelper.isWeiboNull()) {
								SinaWeiboHelper.initWeibo();
							}
							// 判断之前是否登陆过
							if (access != null) {
								SinaWeiboHelper.progressDialog = new ProgressDialog(
										context);
								SinaWeiboHelper.progressDialog
										.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								SinaWeiboHelper.progressDialog
										.setMessage(context
												.getString(R.string.sharing));
								SinaWeiboHelper.progressDialog
										.setCancelable(true);
								SinaWeiboHelper.progressDialog.show();
								new Thread() {
									public void run() {
										SinaWeiboHelper.setAccessToken(
												access.getAccessToken(),
												access.getAccessSecret(),
												access.getExpiresIn());
										SinaWeiboHelper.shareMessage(context,
												shareMessage);
									}
								}.start();
							} else {
								SinaWeiboHelper
										.authorize(context, shareMessage);
							}
							break;
						case 3:// 更多
							showShareMore(context, title, url);
							break;
						}
					}
				});
		builder.create().show();
	}
	
	/**
	 * 调用系统安装了的应用分享
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	public static void showShareMore(Activity context, final String title,
			final String url) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
		intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
		context.startActivity(Intent.createChooser(intent, "选择分享"));
	}

}
