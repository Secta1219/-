package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainFrame extends Frame implements ActionListener, WindowListener {

	ReservationControl reservationControl;

	// ヘッダー
	Panel		panelHeader;
	Label		labelTitle;
	Button		buttonLog;
	TextField	tfLoginID;

	// サイドバー
	Panel		panelSidebar;
	Panel		panelMenuPadded;
	Panel		currentMenuPanel;
	Button		buttonStatusView;
	Button		buttonMyReservation;
	Button		buttonReservation;
	Button		buttonSettings;
	Button		buttonLogout;
	Button		buttonGoToLogin;
	Label		labelUserInfo;
	// ログインフォーム（未ログイン時サイドバー）
	TextField	tfSidebarUserID;
	TextField	tfSidebarPassword;
	Button		buttonSidebarLogin;
	Label		labelLoginError;

	// コンテンツエリア
	Panel		panelContent;
	CardLayout	cardLayout;
	TextArea	textMessage;

	Panel	cardLogin;			// 未ログイン時の中央ログイン画面
	Panel	cardSettings;		// 設定画面
	TextField	tfCurrentPw, tfNewPw, tfConfirmPw;
	Label		labelPwResult;
	Panel	cardStatus;
	Panel	statusCenter;
	Label	labelStatusDate;
	Button	btnCalendar;
	String	currentStatusDate;

	Panel	cardMyRes;
	Panel	cardMessage;

	Color BG        = new Color( 250, 242, 220);
	Color GREEN     = new Color(  67, 160,  71);
	Color SIDEBAR   = new Color( 245, 248, 245);
	Color BUTTON_FG = new Color(  40,  40,  40);	// ボタン・ラベル文字色

	// テーマプリセット {header R,G,B, sidebar R,G,B, bg R,G,B, buttonFg R,G,B}
	static final int[][] THEMES = {
		{  67, 160,  71,  245, 248, 245,  250, 242, 220,   40,  40,  40},	// 緑
		{  41, 128, 185,  240, 245, 252,  242, 248, 255,   40,  40,  40},	// 青
		{  44,  62,  80,   62,  79,  92,  236, 240, 241,  220, 220, 220},	// ダーク
		{ 139, 100,  50,  252, 246, 232,  255, 250, 238,   40,  40,  40},	// ベージュ
	};

	public MainFrame( ReservationControl rc) {
		reservationControl = rc;
		setLayout( new BorderLayout( 0, 0));
		setBackground( GREEN);

		// === ヘッダー ===
		panelHeader = new Panel( new BorderLayout());
		panelHeader.setBackground( GREEN);

		labelTitle = new Label( "  教室予約システム");
		labelTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 18));
		labelTitle.setForeground( BG);

		Panel panelHeaderRight = new Panel();
		panelHeaderRight.setBackground( GREEN);
		buttonLog = new Button( "ログイン");
		buttonLog.setPreferredSize( new Dimension( 90, 28));
		buttonLog.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		tfLoginID = new TextField( "未ログイン", 12);
		tfLoginID.setEditable( false);
		tfLoginID.setFont( new Font( "Noto Sans JP", Font.BOLD, 15));
		tfLoginID.setVisible( false);
		panelHeaderRight.add( tfLoginID);

		panelHeader.add( labelTitle,       BorderLayout.WEST);
		panelHeader.add( panelHeaderRight, BorderLayout.EAST);

		// === サイドバー ===
		panelSidebar = new Panel( new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension( 220, super.getPreferredSize().height);
			}
		};
		panelSidebar.setBackground( SIDEBAR);

		Font btnFont   = new Font( "Noto Sans JP", Font.BOLD, 13);

		buttonStatusView    = new Button( "予約状況確認");
		buttonMyReservation = new Button( "自己予約確認");
		buttonReservation   = new Button( "＋ 新規予約");
		buttonSettings      = new Button( "設定");
		buttonLogout        = new Button( "ログアウト");
		buttonGoToLogin     = new Button( "ログイン画面へ");
		buttonStatusView.setFont( btnFont);
		buttonMyReservation.setFont( btnFont);
		buttonReservation.setFont( btnFont);
		buttonSettings.setFont( btnFont);
		buttonLogout.setFont( btnFont);
		buttonGoToLogin.setFont( btnFont);

		// ログインフォーム部品
		Font labelFont = new Font( "Noto Sans JP", Font.PLAIN, 12);
		Font fieldFont = new Font( "Noto Sans JP", Font.BOLD, 17);
		tfSidebarUserID  = new TextField( "", 12);
		tfSidebarUserID.setFont( fieldFont);
		tfSidebarUserID.setBackground( Color.WHITE);
		tfSidebarPassword = new TextField( "", 12);
		tfSidebarPassword.setFont( fieldFont);
		tfSidebarPassword.setBackground( Color.WHITE);
		tfSidebarPassword.setEchoChar( '*');
		buttonSidebarLogin = new Button( "ログイン");
		buttonSidebarLogin.setFont( btnFont);
		labelLoginError = new Label( "");
		labelLoginError.setFont( new Font( "Noto Sans JP", Font.PLAIN, 11));
		labelLoginError.setForeground( new Color( 200, 50, 50));

		Panel padLeft = new Panel() {
			@Override public Dimension getPreferredSize() { return new Dimension( 12, 0); }
			@Override public Dimension getMinimumSize()  { return new Dimension( 12, 0); }
		};
		padLeft.setBackground( SIDEBAR);
		Panel padRight = new Panel() {
			@Override public Dimension getPreferredSize() { return new Dimension( 12, 0); }
			@Override public Dimension getMinimumSize()  { return new Dimension( 12, 0); }
		};
		padRight.setBackground( SIDEBAR);

		panelMenuPadded = new Panel( new BorderLayout());
		panelMenuPadded.setBackground( SIDEBAR);
		panelMenuPadded.add( padLeft,  BorderLayout.WEST);
		panelMenuPadded.add( padRight, BorderLayout.EAST);

		labelUserInfo = new Label( "  未ログイン");
		labelUserInfo.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		labelUserInfo.setVisible( false);	// 未ログイン時は非表示

		panelSidebar.add( panelMenuPadded, BorderLayout.NORTH);
		panelSidebar.add( labelUserInfo,   BorderLayout.SOUTH);

		// 未ログイン状態のメニューで初期化
		buildMenu( false);

		// === コンテンツエリア ===
		cardLayout   = new CardLayout();
		panelContent = new Panel( cardLayout);
		panelContent.setBackground( BG);

		textMessage = new TextArea( 15, 50);
		textMessage.setEditable( false);
		textMessage.setBackground( BG);

		// === ログインカード（未ログイン時の中央画面）===
		cardLogin = new Panel( new java.awt.GridBagLayout());
		cardLogin.setBackground( BG);

		Color cardBg = new Color( 255, 252, 242);

		int W = 280;	// フォーム幅
		Panel loginBox = new Panel( null);	// 絶対レイアウト
		loginBox.setBackground( cardBg);

		Label loginBigTitle = new Label( "教室予約システム", Label.CENTER);
		loginBigTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 22));
		loginBigTitle.setForeground( GREEN);
		loginBigTitle.setBackground( cardBg);
		loginBigTitle.setBounds( 0, 0, W, 38);

		Label loginSubTitle = new Label( "ログイン", Label.CENTER);
		loginSubTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 15));
		loginSubTitle.setForeground( new Color( 80, 80, 80));
		loginSubTitle.setBackground( cardBg);
		loginSubTitle.setBounds( 0, 48, W, 26);

		Label lblUser = new Label( "  ユーザーID");
		lblUser.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		lblUser.setBackground( cardBg);
		lblUser.setBounds( 0, 90, W, 22);

		tfSidebarUserID.setBounds( 0, 116, W, 24);

		Label lblPass = new Label( "  パスワード");
		lblPass.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		lblPass.setBackground( cardBg);
		lblPass.setBounds( 0, 152, W, 22);

		tfSidebarPassword.setBounds( 0, 178, W, 24);

		buttonSidebarLogin.setBounds( 0, 218, W, 36);
		buttonSidebarLogin.setBackground( GREEN);
		buttonSidebarLogin.setForeground( Color.WHITE);

		labelLoginError.setBackground( cardBg);
		labelLoginError.setBounds( 0, 262, W, 22);

		loginBox.add( loginBigTitle);
		loginBox.add( loginSubTitle);
		loginBox.add( lblUser);
		loginBox.add( tfSidebarUserID);
		loginBox.add( lblPass);
		loginBox.add( tfSidebarPassword);
		loginBox.add( buttonSidebarLogin);
		loginBox.add( labelLoginError);
		loginBox.setPreferredSize( new Dimension( W, 290));

		// カード風に見せるためにパディングパネルで囲む
		Panel cardPad = new Panel( new BorderLayout());
		cardPad.setBackground( cardBg);
		Panel padT = new Panel(); padT.setBackground( cardBg); padT.setPreferredSize( new Dimension( 0, 20));
		Panel padB = new Panel(); padB.setBackground( cardBg); padB.setPreferredSize( new Dimension( 0, 20));
		Panel padL = new Panel(); padL.setBackground( cardBg); padL.setPreferredSize( new Dimension( 30, 0));
		Panel padR = new Panel(); padR.setBackground( cardBg); padR.setPreferredSize( new Dimension( 30, 0));
		cardPad.add( padT,     BorderLayout.NORTH);
		cardPad.add( padB,     BorderLayout.SOUTH);
		cardPad.add( padL,     BorderLayout.WEST);
		cardPad.add( padR,     BorderLayout.EAST);
		cardPad.add( loginBox, BorderLayout.CENTER);

		// 縦横中央に配置
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.anchor  = java.awt.GridBagConstraints.CENTER;
		gbc.fill    = java.awt.GridBagConstraints.NONE;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		cardLogin.add( cardPad, gbc);

		// 予約状況確認カード
		currentStatusDate = new SimpleDateFormat( "yyyy-MM-dd").format( Calendar.getInstance().getTime());
		cardStatus = new Panel( new BorderLayout());
		cardStatus.setBackground( BG);

		Panel statusHeader = new Panel( new BorderLayout());
		statusHeader.setBackground( BG);
		Label labelStatusTitle = new Label( "  予約状況確認");
		labelStatusTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 20));
		labelStatusTitle.setForeground( new Color( 50, 120, 55));

		Panel datePanel = new Panel();
		datePanel.setBackground( BG);
		btnCalendar = new Button( "📅");
		btnCalendar.setFont( new Font( "Segoe UI Emoji", Font.PLAIN, 16));
		btnCalendar.setPreferredSize( new Dimension( 36, 28));
		labelStatusDate = new Label(
				new SimpleDateFormat( "yyyy年MM月dd日").format( Calendar.getInstance().getTime()) + "  ",
				Label.RIGHT);
		labelStatusDate.setFont( new Font( "Noto Sans JP", Font.BOLD, 16));
		labelStatusDate.setForeground( new Color( 80, 80, 80));
		datePanel.add( btnCalendar);
		datePanel.add( labelStatusDate);

		statusHeader.add( labelStatusTitle, BorderLayout.WEST);
		statusHeader.add( datePanel,        BorderLayout.EAST);

		statusCenter = new Panel( new BorderLayout());
		statusCenter.setBackground( BG);
		TextArea taStatus = new TextArea( "← 「予約状況確認」ボタンを押すと表示されます", 5, 40);
		taStatus.setEditable( false);
		taStatus.setBackground( BG);
		statusCenter.add( taStatus, BorderLayout.CENTER);
		cardStatus.add( statusHeader, BorderLayout.NORTH);
		cardStatus.add( statusCenter, BorderLayout.CENTER);

		// 自己予約確認カード
		cardMyRes = new Panel( new BorderLayout());
		cardMyRes.setBackground( BG);
		Label labelMyResTitle = new Label( "  自己予約確認");
		labelMyResTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 20));
		labelMyResTitle.setForeground( new Color( 50, 120, 55));
		TextArea taMyRes = new TextArea( "← 「自己予約確認」ボタンを押すと表示されます", 5, 40);
		taMyRes.setEditable( false);
		taMyRes.setBackground( BG);
		cardMyRes.add( labelMyResTitle, BorderLayout.NORTH);
		cardMyRes.add( taMyRes,         BorderLayout.CENTER);

		// メッセージカード
		cardMessage = new Panel( new BorderLayout());
		cardMessage.setBackground( BG);
		cardMessage.add( textMessage, BorderLayout.CENTER);

		// === 設定カード ===
		cardSettings = new Panel( new BorderLayout());
		cardSettings.setBackground( BG);
		Label labelSettingsTitle = new Label( "  設定");
		labelSettingsTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 20));
		labelSettingsTitle.setForeground( new Color( 50, 120, 55));

		Panel settingsContent = new Panel( new java.awt.GridBagLayout());
		settingsContent.setBackground( BG);

		Panel settingsBox = new Panel( null);
		settingsBox.setBackground( BG);
		int SW = 320;

		// --- パスワード変更セクション ---
		Font sf = new Font( "Noto Sans JP", Font.BOLD, 13);
		Font stf = new Font( "Noto Sans JP", Font.BOLD, 15);
		Font inputFont = new Font( "Noto Sans JP", Font.BOLD, 14);

		Label lblPwTitle = new Label( "パスワード変更");
		lblPwTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 16));
		lblPwTitle.setForeground( GREEN);
		lblPwTitle.setBackground( BG);
		lblPwTitle.setBounds( 0, 0, SW, 28);

		Label lblCur = new Label( "現在のパスワード");
		lblCur.setFont( sf); lblCur.setBackground( BG);
		lblCur.setBounds( 0, 36, SW, 20);
		tfCurrentPw = new TextField( "", 16);
		tfCurrentPw.setEchoChar( '*'); tfCurrentPw.setFont( inputFont);
		tfCurrentPw.setBackground( Color.WHITE);
		tfCurrentPw.setBounds( 0, 60, SW, 26);

		Label lblNew = new Label( "新しいパスワード");
		lblNew.setFont( sf); lblNew.setBackground( BG);
		lblNew.setBounds( 0, 96, SW, 20);
		tfNewPw = new TextField( "", 16);
		tfNewPw.setEchoChar( '*'); tfNewPw.setFont( inputFont);
		tfNewPw.setBackground( Color.WHITE);
		tfNewPw.setBounds( 0, 120, SW, 26);

		Label lblConf = new Label( "新しいパスワード（確認）");
		lblConf.setFont( sf); lblConf.setBackground( BG);
		lblConf.setBounds( 0, 156, SW, 20);
		tfConfirmPw = new TextField( "", 16);
		tfConfirmPw.setEchoChar( '*'); tfConfirmPw.setFont( inputFont);
		tfConfirmPw.setBackground( Color.WHITE);
		tfConfirmPw.setBounds( 0, 180, SW, 26);

		Button btnChangePw = new Button( "パスワードを変更");
		btnChangePw.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		btnChangePw.setBackground( GREEN);
		btnChangePw.setForeground( Color.WHITE);
		btnChangePw.setBounds( 0, 220, SW, 32);

		labelPwResult = new Label( "");
		labelPwResult.setFont( new Font( "Noto Sans JP", Font.PLAIN, 12));
		labelPwResult.setBackground( BG);
		labelPwResult.setBounds( 0, 260, SW, 20);

		// --- テーマ変更セクション ---
		Label lblThemeTitle = new Label( "テーマ");
		lblThemeTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 16));
		lblThemeTitle.setForeground( GREEN);
		lblThemeTitle.setBackground( BG);
		lblThemeTitle.setBounds( 0, 300, SW, 28);

		String[] themeNames = { "グリーン", "ブルー", "ダーク", "ベージュ"};
		Color[] themePrimary = {
			new Color( 67, 160, 71), new Color( 41, 128, 185),
			new Color( 44,  62, 80), new Color( 139, 100, 50)
		};
		int btnW = (SW - 12) / 4;
		for( int i = 0; i < 4; i++) {
			final int themeId = i;
			Button tb = new Button( themeNames[i]);
			tb.setFont( new Font( "Noto Sans JP", Font.BOLD, 12));
			tb.setBackground( themePrimary[i]);
			tb.setForeground( Color.WHITE);
			tb.setBounds( i * (btnW + 4), 336, btnW, 36);
			tb.addActionListener( ev -> applyTheme( themeId));
			settingsBox.add( tb);
		}

		settingsBox.add( lblPwTitle); settingsBox.add( lblCur);
		settingsBox.add( tfCurrentPw); settingsBox.add( lblNew);
		settingsBox.add( tfNewPw); settingsBox.add( lblConf);
		settingsBox.add( tfConfirmPw); settingsBox.add( btnChangePw);
		settingsBox.add( labelPwResult);
		settingsBox.add( lblThemeTitle);
		settingsBox.setPreferredSize( new Dimension( SW, 380));

		java.awt.GridBagConstraints sgbc = new java.awt.GridBagConstraints();
		sgbc.anchor = java.awt.GridBagConstraints.CENTER;
		sgbc.fill   = java.awt.GridBagConstraints.NONE;
		sgbc.weightx = 1.0; sgbc.weighty = 1.0;
		settingsContent.add( settingsBox, sgbc);

		cardSettings.add( labelSettingsTitle, BorderLayout.NORTH);
		cardSettings.add( settingsContent,    BorderLayout.CENTER);

		btnChangePw.addActionListener( this);

		panelContent.add( cardLogin,    "login");
		panelContent.add( cardStatus,   "status");
		panelContent.add( cardMyRes,    "myres");
		panelContent.add( cardMessage,  "message");
		panelContent.add( cardSettings, "settings");

		cardLayout.show( panelContent, "login");

		// === フレームに追加 ===
		add( panelHeader,  BorderLayout.NORTH);
		add( panelSidebar, BorderLayout.WEST);
		add( panelContent, BorderLayout.CENTER);

		// === リスナー登録 ===
		// EnterキーでログインできるようにKeyListenerを追加
		KeyAdapter loginOnEnter = new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER) {
					buttonSidebarLogin.dispatchEvent(
						new java.awt.event.ActionEvent( buttonSidebarLogin,
							java.awt.event.ActionEvent.ACTION_PERFORMED, ""));
				}
			}
		};
		tfSidebarUserID.addKeyListener( loginOnEnter);
		tfSidebarPassword.addKeyListener( loginOnEnter);

		buttonLog.addActionListener( this);
		buttonLogout.addActionListener( this);
		buttonGoToLogin.addActionListener( this);
		buttonSidebarLogin.addActionListener( this);
		btnCalendar.addActionListener( this);
		buttonStatusView.addActionListener( this);
		buttonMyReservation.addActionListener( this);
		buttonReservation.addActionListener( this);
		buttonSettings.addActionListener( this);
		addWindowListener( this);
	}

	// ログイン状態に応じてサイドバーメニューを再構築
	private void buildMenu( boolean loggedIn) {
		if( currentMenuPanel != null) {
			panelMenuPadded.remove( currentMenuPanel);
		}
		Font titleFont = new Font( "Noto Sans JP", Font.BOLD, 14);
		Font labelFont = new Font( "Noto Sans JP", Font.PLAIN, 12);
		Panel menu = new Panel( new GridLayout( 0, 1, 0, 5));
		menu.setBackground( SIDEBAR);

		if( !loggedIn) {
			Label menuTitle = new Label( "  メニュー");
			menuTitle.setFont( titleFont);
			menuTitle.setBackground( SIDEBAR);
			menuTitle.setForeground( BUTTON_FG);
			Label sp1 = new Label( ""); sp1.setBackground( SIDEBAR);
			menu.add( sp1);
			menu.add( menuTitle);
			menu.add( buttonStatusView);
			menu.add( buttonGoToLogin);

		} else {
			Label menuTitle = new Label( "  メニュー");
			menuTitle.setFont( titleFont);
			menuTitle.setBackground( SIDEBAR);
			menuTitle.setForeground( BUTTON_FG);
			Label sp1 = new Label( ""); sp1.setBackground( SIDEBAR);
			Label sp2 = new Label( ""); sp2.setBackground( SIDEBAR);
			Label adminTitle = new Label( "  管理");
			adminTitle.setFont( titleFont);
			adminTitle.setBackground( SIDEBAR);
			adminTitle.setForeground( BUTTON_FG);
			menu.add( sp1);
			menu.add( menuTitle);
			menu.add( buttonStatusView);
			menu.add( buttonMyReservation);
			menu.add( buttonReservation);
			menu.add( sp2);
			menu.add( adminTitle);
			menu.add( buttonSettings);
			menu.add( buttonLogout);
		}

		currentMenuPanel = menu;
		panelMenuPadded.add( currentMenuPanel, BorderLayout.CENTER);
		panelMenuPadded.validate();
		panelSidebar.validate();
		validate();
	}

	// テーマを適用する
	private void applyTheme( int themeId) {
		int[] t = THEMES[themeId];
		Color oldGreen   = GREEN;
		Color oldSidebar = SIDEBAR;
		Color oldBG      = BG;
		GREEN     = new Color( t[0], t[1], t[2]);
		SIDEBAR   = new Color( t[3], t[4], t[5]);
		BG        = new Color( t[6], t[7], t[8]);
		BUTTON_FG = new Color( t[9], t[10], t[11]);
		recolorBg( this, oldGreen,   GREEN);
		recolorBg( this, oldSidebar, SIDEBAR);
		recolorBg( this, oldBG,      BG);
		labelTitle.setForeground( BG);
		buildMenu( reservationControl.isLoggedIn());	// メニューを新色で再構築
		repaint();
	}

	// コンポーネントを再帰的に背景色を更新する
	private void recolorBg( java.awt.Component c, Color oldC, Color newC) {
		if( oldC.equals( c.getBackground())) c.setBackground( newC);
		if( c instanceof java.awt.Container) {
			for( java.awt.Component child : ((java.awt.Container) c).getComponents()) {
				recolorBg( child, oldC, newC);
			}
		}
	}

	// グリッドを再描画する
	private void refreshStatusGrid() {
		statusCenter.removeAll();
		ScrollPane sp = new ScrollPane( ScrollPane.SCROLLBARS_AS_NEEDED);
		sp.add( buildReservationGrid( currentStatusDate));
		statusCenter.add( sp, BorderLayout.CENTER);
		statusCenter.validate();
		cardLayout.show( panelContent, "status");
	}

	// 予約状況グリッドを構築して返す
	private Panel buildReservationGrid( String date) {
		ArrayList<String>   facilities   = reservationControl.getFacilityId();
		ArrayList<String[]> reservations = reservationControl.getReservationsForDate( date);
		Collections.sort( facilities);

		int startHour = 8;
		int endHour   = 21;

		Color headerBg = new Color( 220, 215, 195);
		Color emptyBg  = BG;
		Color ownBg    = new Color( 190, 230, 195);
		Color otherBg  = new Color( 255, 215, 160);
		Color gridLine = new Color( 190, 195, 185);

		int numRows = ( endHour - startHour) + 1;
		int numCols = facilities.size() + 1;

		Panel grid = new Panel( new GridLayout( numRows, numCols, 1, 1));
		grid.setBackground( gridLine);

		Font headerFont = new Font( "Noto Sans JP", Font.BOLD,  12);
		Font cellFont   = new Font( "Noto Sans JP", Font.PLAIN, 12);

		Label corner = new Label( "", Label.CENTER);
		corner.setBackground( headerBg);
		grid.add( corner);
		for( String fid : facilities) {
			Label h = new Label( fid, Label.CENTER);
			h.setBackground( headerBg);
			h.setFont( headerFont);
			grid.add( h);
		}

		for( int hour = startHour; hour < endHour; hour++) {
			Label timeLabel = new Label( String.format( "%02d:00", hour), Label.CENTER);
			timeLabel.setBackground( headerBg);
			timeLabel.setFont( headerFont);
			grid.add( timeLabel);

			for( String fid : facilities) {
				Label cell = new Label( "", Label.CENTER);
				cell.setBackground( emptyBg);
				cell.setFont( cellFont);

				for( String[] res : reservations) {
					if( res[0].equals( fid)) {
						int rStart = Integer.parseInt( res[2].substring( 0, 2));
						int rEnd   = Integer.parseInt( res[3].substring( 0, 2));
						if( hour >= rStart && hour < rEnd) {
							if( reservationControl.isLoggedIn()) {
								cell.setText( " " + res[1]);
								boolean isOwn = res[1].equals( reservationControl.reservationUserID);
								cell.setBackground( isOwn ? ownBg : otherBg);
							} else {
								cell.setText( " 予約済");
								cell.setBackground( otherBg);
							}
							break;
						}
					}
				}
				grid.add( cell);
			}
		}
		return grid;
	}

	@Override
	public void actionPerformed( ActionEvent e) {
		String result = "";
		if( e.getSource() == buttonSidebarLogin) {
			// サイドバーのログインフォームから認証
			String userId = tfSidebarUserID.getText().trim();
			String pass   = tfSidebarPassword.getText();
			result = reservationControl.loginWithCredentials( userId, pass, this);
			if( result.isEmpty()) {
				tfLoginID.setVisible( true);
				labelUserInfo.setText( "  " + tfLoginID.getText());
				labelUserInfo.setVisible( true);
				buildMenu( true);
				refreshStatusGrid();
			} else {
				labelLoginError.setText( "  " + result);
				panelMenuPadded.validate();
			}

		} else if( e.getSource() == buttonLogout || e.getSource() == buttonLog) {
			reservationControl.loginLogout( this);
			tfLoginID.setVisible( false);
			labelUserInfo.setText( "  未ログイン");
			labelUserInfo.setVisible( false);
			tfSidebarUserID.setText( "");
			tfSidebarPassword.setText( "");
			labelLoginError.setText( "");
			buildMenu( false);
			cardLayout.show( panelContent, "login");

		} else if( e.getSource() == btnCalendar) {
			Calendar cal = Calendar.getInstance();
			CalendarDialog cd = new CalendarDialog( this,
					cal.get( Calendar.YEAR), cal.get( Calendar.MONTH));
			java.awt.Point p = btnCalendar.getLocationOnScreen();
			int calW    = 310;
			int calX    = p.x + btnCalendar.getWidth() - calW;
			int screenW = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			if( calX + calW > screenW) calX = screenW - calW - 5;
			if( calX < 0)             calX = 0;
			cd.setLocation( calX, p.y + btnCalendar.getHeight());
			cd.setVisible( true);
			if( cd.selectedDate != null) {
				currentStatusDate = cd.selectedDate;
				String[] parts = currentStatusDate.split( "-");
				labelStatusDate.setText( parts[0] + "年" + parts[1] + "月" + parts[2] + "日  ");
				refreshStatusGrid();
			}

		} else if( e.getSource() == buttonGoToLogin) {
			cardLayout.show( panelContent, "login");

		} else if( e.getSource() == buttonStatusView) {
			currentStatusDate = new SimpleDateFormat( "yyyy-MM-dd").format( Calendar.getInstance().getTime());
			labelStatusDate.setText(
					new SimpleDateFormat( "yyyy年MM月dd日").format( Calendar.getInstance().getTime()) + "  ");
			refreshStatusGrid();

		} else if( e.getSource() == buttonMyReservation) {
			result = reservationControl.getMyReservations();
			textMessage.setText( result);
			cardLayout.show( panelContent, "message");

		} else if( e.getSource() == buttonReservation) {
			result = reservationControl.makeReservation( this);
			textMessage.setText( result);
			cardLayout.show( panelContent, "message");

		} else if( e.getSource() == buttonSettings) {
			tfCurrentPw.setText( ""); tfNewPw.setText( ""); tfConfirmPw.setText( "");
			labelPwResult.setText( "");
			cardLayout.show( panelContent, "settings");

		} else if( e.getActionCommand().equals( "パスワードを変更")) {
			String cur  = tfCurrentPw.getText();
			String nw   = tfNewPw.getText();
			String conf = tfConfirmPw.getText();
			if( nw.isEmpty()) {
				labelPwResult.setText( "新しいパスワードを入力してください。");
				labelPwResult.setForeground( new Color( 200, 50, 50));
			} else if( !nw.equals( conf)) {
				labelPwResult.setText( "新しいパスワードが一致しません。");
				labelPwResult.setForeground( new Color( 200, 50, 50));
			} else {
				result = reservationControl.changePassword( cur, nw);
				labelPwResult.setText( result.isEmpty() ? "パスワードを変更しました。" : result);
				labelPwResult.setForeground( result.isEmpty()
						? new Color( 50, 130, 50) : new Color( 200, 50, 50));
				if( result.isEmpty()) {
					tfCurrentPw.setText( ""); tfNewPw.setText( ""); tfConfirmPw.setText( "");
				}
			}
		}
	}

	@Override public void windowClosing( WindowEvent e)     { System.exit( 0); }
	@Override
	public void windowOpened( WindowEvent e) {
		cardLayout.show( panelContent, "login");
	}
	@Override public void windowClosed( WindowEvent e)      {}
	@Override public void windowIconified( WindowEvent e)   {}
	@Override public void windowDeiconified( WindowEvent e) {}
	@Override public void windowActivated( WindowEvent e)   {}
	@Override public void windowDeactivated( WindowEvent e) {}
}
