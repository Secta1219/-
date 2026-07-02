package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog;
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
import java.util.List;

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
	Button		buttonFacilityInfo;
	Button		buttonMyReservation;
	Button		buttonReservation;
	Button		buttonSettings;
	Button		buttonLogout;
	Button		buttonGoToLogin;
	Label		labelUserInfo;
	// ログインフォーム（未ログイン時サイドバー）
	TextField		tfSidebarUserID;
	TextField		tfSidebarPassword;
	Button			buttonSidebarLogin;
	Label			labelLoginError;
	java.awt.Checkbox	cbRememberMe;	// ログイン状態を保存するチェックボックス

	static final String CREDS_FILE = System.getProperty( "user.home") + "/.reservation_credentials.txt";

	// コンテンツエリア
	Panel		panelContent;
	CardLayout	cardLayout;
	TextArea	textMessage;

	Panel	cardLogin;			// 未ログイン時の中央ログイン画面
	Panel	cardFacilityInfo;
	ChoiceFacility fiChoice;
	TextArea	fiText;
	Panel		cardNewRes;			// 新規予約ページ
	Panel		nrTabContent;
	CardLayout	nrTabLayout;
	ChoiceFacility	nrFacility;
	TextField	nrYear, nrMonth, nrDay;
	ChoiceHour	nrStartHour, nrEndHour;
	ChoiceMinute	nrStartMin, nrEndMin;
	Label		nrResult;
	TextField	nrCsvPath;
	Label		nrCsvResult;
	Panel		nrCsvDetail;	// 結果テーブルを差し替えるコンテナ
	Button		nrBtnManual, nrBtnCsv;

	Panel		cardSettings;		// 設定画面
	TextField	tfCurrentPw, tfNewPw, tfConfirmPw;
	Label		labelPwResult;
	Panel	cardStatus;
	Panel	statusCenter;
	Label	labelStatusDate;
	TextField	tfStatusDate;
	CardLayout	dateCardLayout;
	Panel		dateCardPanel;
	Button		btnFloorFilter;
	java.util.Set<String>	selectedFloors = new java.util.TreeSet<>();
	Button	btnCalendar;
	Button	btnPrevDay;
	Button	btnNextDay;
	String	currentStatusDate;

	Panel	cardMyRes;
	Panel	myResCenter;
	int		myResSortCol = 1;	// 1=日付（デフォルト）, 2=教室
	boolean	myResSortAsc = true;
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
		buttonFacilityInfo  = new Button( "教室概要");
		buttonMyReservation = new Button( "自己予約確認");
		buttonReservation   = new Button( "＋ 新規予約");
		buttonSettings      = new Button( "設定");
		buttonLogout        = new Button( "ログアウト");
		buttonGoToLogin     = new Button( "ログイン画面へ");
		buttonStatusView.setFont( btnFont);
		buttonFacilityInfo.setFont( btnFont);
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

		cbRememberMe = new java.awt.Checkbox( "ログイン状態を保存");
		cbRememberMe.setFont( new Font( "Noto Sans JP", Font.PLAIN, 12));

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

		cbRememberMe.setBackground( cardBg);
		cbRememberMe.setBounds( 0, 208, W, 22);

		buttonSidebarLogin.setBounds( 0, 240, W, 36);
		buttonSidebarLogin.setBackground( GREEN);
		buttonSidebarLogin.setForeground( Color.WHITE);

		labelLoginError.setBackground( cardBg);
		labelLoginError.setBounds( 0, 284, W, 22);

		loginBox.add( loginBigTitle);
		loginBox.add( loginSubTitle);
		loginBox.add( lblUser);
		loginBox.add( tfSidebarUserID);
		loginBox.add( lblPass);
		loginBox.add( tfSidebarPassword);
		loginBox.add( cbRememberMe);
		loginBox.add( buttonSidebarLogin);
		loginBox.add( labelLoginError);
		loginBox.setPreferredSize( new Dimension( W, 310));

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

		Panel datePanel = new Panel( new java.awt.FlowLayout( java.awt.FlowLayout.CENTER, 8, 4));
		datePanel.setBackground( BG);

		btnPrevDay = new Button( "＜");
		btnPrevDay.setFont( new Font( "Yu Gothic UI", Font.BOLD, 16));
		btnPrevDay.setPreferredSize( new Dimension( 40, 32));

		String initDate = new SimpleDateFormat( "yyyy年MM月dd日").format( Calendar.getInstance().getTime());
		String initDateInput = new SimpleDateFormat( "yyyy/MM/dd").format( Calendar.getInstance().getTime());

		labelStatusDate = new Label( initDate, Label.CENTER);
		labelStatusDate.setFont( new Font( "Noto Sans JP", Font.BOLD, 18));
		labelStatusDate.setForeground( new Color( 80, 80, 80));

		tfStatusDate = new TextField( initDateInput);
		tfStatusDate.setFont( new Font( "Noto Sans JP", Font.BOLD, 18));
		tfStatusDate.setForeground( new Color( 80, 80, 80));
		tfStatusDate.setBackground( Color.WHITE);

		dateCardLayout = new CardLayout();
		dateCardPanel = new Panel( dateCardLayout);
		dateCardPanel.setBackground( BG);
		dateCardPanel.setPreferredSize( new Dimension( 200, 32));
		dateCardPanel.add( labelStatusDate, "label");
		dateCardPanel.add( tfStatusDate,    "edit");
		dateCardLayout.show( dateCardPanel, "label");

		// Label クリックで TextField に切替
		labelStatusDate.addMouseListener( new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked( java.awt.event.MouseEvent e) {
				String[] dp = currentStatusDate.split( "-");
				tfStatusDate.setText( dp[0] + "/" + dp[1] + "/" + dp[2]);
				dateCardLayout.show( dateCardPanel, "edit");
				tfStatusDate.requestFocus();
				tfStatusDate.selectAll();
			}
		});
		labelStatusDate.setCursor( java.awt.Cursor.getPredefinedCursor( java.awt.Cursor.HAND_CURSOR));

		// TextField: Enter で確定、ESC でキャンセル、Focus離脱で確定
		tfStatusDate.addKeyListener( new java.awt.event.KeyAdapter() {
			@Override
			public void keyPressed( java.awt.event.KeyEvent e) {
				if( e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) commitDateInput();
				else if( e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) cancelDateInput();
			}
		});
		tfStatusDate.addFocusListener( new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost( java.awt.event.FocusEvent e) { commitDateInput(); }
		});

		btnNextDay = new Button( "＞");
		btnNextDay.setFont( new Font( "Yu Gothic UI", Font.BOLD, 16));
		btnNextDay.setPreferredSize( new Dimension( 40, 32));

		btnCalendar = new Button( "カレンダー");
		btnCalendar.setFont( new Font( "Noto Sans JP", Font.BOLD, 12));
		btnCalendar.setPreferredSize( new Dimension( 90, 32));

		btnFloorFilter = new Button( "階フィルタ");
		btnFloorFilter.setFont( new Font( "Noto Sans JP", Font.BOLD, 12));
		btnFloorFilter.setPreferredSize( new Dimension( 100, 32));

		// 初期値：全階を選択状態にしておく
		for( String fid : rc.getFacilityId()) {
			if( fid != null && fid.length() >= 2) selectedFloors.add( fid.substring( 0, 2));
		}

		datePanel.add( btnPrevDay);
		datePanel.add( dateCardPanel);
		datePanel.add( btnNextDay);
		datePanel.add( new Label( "  "));	// スペーサー
		datePanel.add( btnCalendar);
		datePanel.add( btnFloorFilter);

		statusHeader.add( labelStatusTitle, BorderLayout.WEST);
		statusHeader.add( datePanel,        BorderLayout.CENTER);

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
		Label labelMyResTitle = new Label( "  自己予約確認（本日以降）");
		labelMyResTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 20));
		labelMyResTitle.setForeground( new Color( 50, 120, 55));
		myResCenter = new Panel( new BorderLayout());
		myResCenter.setBackground( BG);
		cardMyRes.add( labelMyResTitle, BorderLayout.NORTH);
		cardMyRes.add( myResCenter,     BorderLayout.CENTER);

		// === 新規予約カード ===
		cardNewRes = new Panel( new BorderLayout());
		cardNewRes.setBackground( BG);

		// タイトル＋タブ切替ボタン
		Panel nrNorth = new Panel( new BorderLayout());
		nrNorth.setBackground( BG);
		Label nrTitle = new Label( "  新規予約");
		nrTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 20));
		nrTitle.setForeground( new Color( 50, 120, 55));
		Panel nrTabs = new Panel();
		nrTabs.setBackground( BG);
		nrBtnManual = new Button( "手動入力");
		nrBtnCsv    = new Button( "CSV一括");
		nrBtnManual.setFont( new Font( "Noto Sans JP", Font.BOLD, 12));
		nrBtnCsv.setFont(    new Font( "Noto Sans JP", Font.BOLD, 12));
		nrTabs.add( nrBtnManual); nrTabs.add( nrBtnCsv);
		nrNorth.add( nrTitle, BorderLayout.WEST);
		nrNorth.add( nrTabs,  BorderLayout.EAST);
		cardNewRes.add( nrNorth, BorderLayout.NORTH);

		// タブコンテンツ
		nrTabLayout  = new CardLayout();
		nrTabContent = new Panel( nrTabLayout);
		nrTabContent.setBackground( BG);

		// --- 手動入力タブ ---
		Panel manualTab = new Panel( new java.awt.GridBagLayout());
		manualTab.setBackground( BG);
		Panel manualBox = new Panel( null);
		manualBox.setBackground( BG);
		int MW = 360;
		Font mf = new Font( "Noto Sans JP", Font.BOLD, 13);
		Font mif = new Font( "Noto Sans JP", Font.PLAIN, 14);

		List<String> nrFacIds = new ArrayList<String>();
		nrFacIds = rc.getFacilityId();
		nrFacility = new ChoiceFacility( nrFacIds);
		nrFacility.setFont( mif);

		nrYear  = new TextField( "", 6);  nrYear.setFont( mif);  nrYear.setBackground( Color.WHITE);
		nrMonth = new TextField( "", 2);  nrMonth.setFont( mif); nrMonth.setBackground( Color.WHITE);
		nrDay   = new TextField( "", 2);  nrDay.setFont( mif);   nrDay.setBackground( Color.WHITE);
		// 文字数制限と範囲制限（年:4桁、月:1-12、日:1-31）
		limitTextLength( nrYear,  4);
		limitTextLength( nrMonth, 2, 12);
		limitTextLength( nrDay,   2, 31);
		nrStartHour = new ChoiceHour();   nrStartHour.setFont( mif);
		nrStartMin  = new ChoiceMinute(); nrStartMin.setFont( mif);
		nrEndHour   = new ChoiceHour();   nrEndHour.setFont( mif);
		nrEndMin    = new ChoiceMinute(); nrEndMin.setFont( mif);

		Label lFac = new Label( "教室"); lFac.setFont( mf); lFac.setBackground( BG); lFac.setBounds( 0, 0, MW, 22);
		nrFacility.setBounds( 0, 26, 200, 26);
		Label lDay = new Label( "予約日"); lDay.setFont( mf); lDay.setBackground( BG); lDay.setBounds( 0, 68, MW, 22);
		nrYear.setBounds(  0, 94, 80, 26); Label ly = new Label("年"); ly.setFont(mf); ly.setBackground(BG); ly.setBounds(84,94,20,26);
		nrMonth.setBounds(108,94, 50, 26); Label lm = new Label("月"); lm.setFont(mf); lm.setBackground(BG); lm.setBounds(162,94,20,26);
		nrDay.setBounds(  186,94, 50, 26); Label ld = new Label("日"); ld.setFont(mf); ld.setBackground(BG); ld.setBounds(240,94,20,26);
		Label lSt = new Label( "開始"); lSt.setFont( mf); lSt.setBackground( BG); lSt.setBounds( 0, 136, MW, 22);
		nrStartHour.setBounds( 0,162, 80,26); Label lsh=new Label("時"); lsh.setFont(mf); lsh.setBackground(BG); lsh.setBounds(84,162,20,26);
		nrStartMin.setBounds( 108,162, 80,26); Label lsm=new Label("分"); lsm.setFont(mf); lsm.setBackground(BG); lsm.setBounds(192,162,20,26);
		Label lEt = new Label( "終了"); lEt.setFont( mf); lEt.setBackground( BG); lEt.setBounds( 0, 204, MW, 22);
		nrEndHour.setBounds( 0,230, 80,26); Label leh=new Label("時"); leh.setFont(mf); leh.setBackground(BG); leh.setBounds(84,230,20,26);
		nrEndMin.setBounds( 108,230, 80,26); Label lem=new Label("分"); lem.setFont(mf); lem.setBackground(BG); lem.setBounds(192,230,20,26);

		Button nrSubmit = new Button( "予約する");
		nrSubmit.setFont( new Font( "Noto Sans JP", Font.BOLD, 14));
		nrSubmit.setBackground( GREEN); nrSubmit.setForeground( Color.WHITE);
		nrSubmit.setBounds( 0, 276, 200, 36);
		nrResult = new Label( ""); nrResult.setFont( mf); nrResult.setBackground( BG); nrResult.setBounds( 0, 322, MW + 200, 22);

		for( java.awt.Component c : new java.awt.Component[]{ lFac, nrFacility, lDay,
				nrYear, ly, nrMonth, lm, nrDay, ld, lSt, nrStartHour, lsh, nrStartMin, lsm,
				lEt, nrEndHour, leh, nrEndMin, lem, nrSubmit, nrResult}) manualBox.add( c);
		manualBox.setPreferredSize( new Dimension( MW + 200, 350));
		java.awt.GridBagConstraints mgbc = new java.awt.GridBagConstraints();
		mgbc.anchor = java.awt.GridBagConstraints.CENTER; mgbc.fill = java.awt.GridBagConstraints.NONE;
		mgbc.weightx = 1.0; mgbc.weighty = 1.0;
		manualTab.add( manualBox, mgbc);

		// --- CSV一括タブ ---
		Panel csvTab = new Panel( new java.awt.GridBagLayout());
		csvTab.setBackground( BG);
		Panel csvBox = new Panel( null);
		csvBox.setBackground( BG);
		int CW = 800;

		Label lCsvTitle = new Label( "CSVファイルで一括予約");
		lCsvTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 16)); lCsvTitle.setForeground( new Color( 50,120,55));
		lCsvTitle.setBackground( BG); lCsvTitle.setBounds( 0, 0, CW, 28);

		Label lCsvFmt = new Label( "形式: facility_id, day(yyyy-MM-dd), start(HH:mm), end(HH:mm)");
		lCsvFmt.setFont( new Font( "Noto Sans JP", Font.PLAIN, 12)); lCsvFmt.setBackground( BG); lCsvFmt.setBounds( 0, 36, CW, 20);

		Button nrBtnTemplate = new Button( "テンプレートをダウンロードに保存");
		nrBtnTemplate.setFont( mf); nrBtnTemplate.setBounds( 0, 64, CW, 30);

		Label lCsvPath = new Label( "CSVファイルパス:"); lCsvPath.setFont( mf); lCsvPath.setBackground( BG); lCsvPath.setBounds( 0, 110, CW, 22);
		nrCsvPath = new TextField( "", 30); nrCsvPath.setFont( mif); nrCsvPath.setBackground( Color.WHITE); nrCsvPath.setBounds( 0, 136, 300, 28);
		Button nrBtnBrowse = new Button( "参照..."); nrBtnBrowse.setFont( mf); nrBtnBrowse.setBounds( 308, 136, 80, 28);
		Button nrBtnImport = new Button( "インポート");
		nrBtnImport.setFont( new Font( "Noto Sans JP", Font.BOLD, 14));
		nrBtnImport.setBackground( GREEN); nrBtnImport.setForeground( Color.WHITE); nrBtnImport.setBounds( 0, 180, 200, 36);
		nrCsvResult = new Label( ""); nrCsvResult.setFont( mf); nrCsvResult.setBackground( BG); nrCsvResult.setBounds( 0, 226, CW, 22);

		Label lDetailTitle = new Label( "詳細"); lDetailTitle.setFont( mf); lDetailTitle.setBackground( BG); lDetailTitle.setBounds( 0, 258, CW, 22);
		nrCsvDetail = new Panel( new BorderLayout());
		nrCsvDetail.setBackground( BG);
		nrCsvDetail.setBounds( 0, 284, CW, 380);

		for( java.awt.Component c : new java.awt.Component[]{ lCsvTitle, lCsvFmt, nrBtnTemplate,
				lCsvPath, nrCsvPath, nrBtnBrowse, nrBtnImport, nrCsvResult,
				lDetailTitle, nrCsvDetail}) csvBox.add( c);
		csvBox.setPreferredSize( new Dimension( CW, 680));
		java.awt.GridBagConstraints cgbc = new java.awt.GridBagConstraints();
		cgbc.anchor = java.awt.GridBagConstraints.CENTER; cgbc.fill = java.awt.GridBagConstraints.NONE;
		cgbc.weightx = 1.0; cgbc.weighty = 1.0;
		csvTab.add( csvBox, cgbc);

		nrTabContent.add( manualTab, "manual");
		nrTabContent.add( csvTab,    "csv");
		nrTabLayout.show( nrTabContent, "manual");
		cardNewRes.add( nrTabContent, BorderLayout.CENTER);

		// ボタンアクション登録
		nrBtnManual.addActionListener( this);
		nrBtnCsv.addActionListener( this);
		nrSubmit.addActionListener( this);
		nrBtnTemplate.addActionListener( this);
		nrBtnBrowse.addActionListener( this);
		nrBtnImport.addActionListener( this);

		// 施設変更時に時間帯を更新
		nrFacility.addItemListener( ev -> {
			int[][] t = reservationControl.getAvailableTime( nrFacility.getSelectedItem());
			nrStartHour.resetRange( t[0][0], t[1][0]);
			nrEndHour.resetRange(   t[0][0], t[1][0]);
		});
		if( !nrFacIds.isEmpty()) {
			int[][] t = rc.getAvailableTime( nrFacility.getSelectedItem());
			nrStartHour.resetRange( t[0][0], t[1][0]);
			nrEndHour.resetRange(   t[0][0], t[1][0]);
		}

		// メッセージカード
		cardMessage = new Panel( new BorderLayout());
		cardMessage.setBackground( BG);
		cardMessage.add( textMessage, BorderLayout.CENTER);

		// === 設定カード ===
		// === 教室概要カード ===
		cardFacilityInfo = new Panel( new BorderLayout( 0, 0));
		cardFacilityInfo.setBackground( BG);
		Label labelFiTitle = new Label( "  教室概要");
		labelFiTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 22));
		labelFiTitle.setForeground( new Color( 50, 120, 55));
		labelFiTitle.setBackground( BG);
		cardFacilityInfo.add( labelFiTitle, BorderLayout.NORTH);

		// 左カラム: 教室一覧（縦並びのボタン）
		Panel fiLeft = new Panel( new BorderLayout());
		fiLeft.setBackground( BG);
		fiLeft.setPreferredSize( new Dimension( 180, 0));

		Label fiListTitle = new Label( "  教室一覧");
		fiListTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 14));
		fiListTitle.setForeground( new Color( 100, 100, 100));
		fiListTitle.setBackground( BG);
		fiLeft.add( fiListTitle, BorderLayout.NORTH);

		ArrayList<String> fiIds = rc.getFacilityId();
		Collections.sort( fiIds);
		Panel fiListPanel = new Panel( new GridLayout( 0, 1, 0, 6));
		fiListPanel.setBackground( BG);

		// 中央カラム: 教室詳細カード
		Panel fiRight = new Panel( new java.awt.GridBagLayout());
		fiRight.setBackground( BG);

		Color fiCardBg = new Color( 255, 252, 242);
		Panel fiCard = new Panel( null);
		fiCard.setBackground( fiCardBg);
		fiCard.setPreferredSize( new Dimension( 540, 440));

		Label fiCardRoomId = new Label( "", Label.CENTER);
		fiCardRoomId.setFont( new Font( "Noto Sans JP", Font.BOLD, 56));
		fiCardRoomId.setForeground( GREEN);
		fiCardRoomId.setBackground( fiCardBg);
		fiCardRoomId.setBounds( 30, 30, 460, 80);

		Label fiCardRoomName = new Label( "", Label.CENTER);
		fiCardRoomName.setFont( new Font( "Noto Sans JP", Font.BOLD, 20));
		fiCardRoomName.setForeground( new Color( 80, 80, 80));
		fiCardRoomName.setBackground( fiCardBg);
		fiCardRoomName.setBounds( 30, 120, 460, 30);

		Label fiCardSeatsTitle = new Label( "  座席数", Label.LEFT);
		fiCardSeatsTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		fiCardSeatsTitle.setForeground( new Color( 130, 130, 130));
		fiCardSeatsTitle.setBackground( fiCardBg);
		fiCardSeatsTitle.setBounds( 80, 180, 160, 22);

		Label fiCardSeats = new Label( "  ―", Label.LEFT);
		fiCardSeats.setFont( new Font( "Noto Sans JP", Font.BOLD, 22));
		fiCardSeats.setForeground( new Color( 60, 60, 60));
		fiCardSeats.setBackground( fiCardBg);
		fiCardSeats.setBounds( 80, 202, 160, 34);

		Label fiCardTimeTitle = new Label( "  利用可能時間", Label.LEFT);
		fiCardTimeTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		fiCardTimeTitle.setForeground( new Color( 130, 130, 130));
		fiCardTimeTitle.setBackground( fiCardBg);
		fiCardTimeTitle.setBounds( 270, 180, 240, 22);

		Label fiCardTime = new Label( "  ―", Label.LEFT);
		fiCardTime.setFont( new Font( "Noto Sans JP", Font.BOLD, 22));
		fiCardTime.setForeground( new Color( 60, 60, 60));
		fiCardTime.setBackground( fiCardBg);
		fiCardTime.setBounds( 270, 202, 240, 34);

		fiCard.add( fiCardRoomId);
		fiCard.add( fiCardRoomName);
		fiCard.add( fiCardSeatsTitle); fiCard.add( fiCardSeats);
		fiCard.add( fiCardTimeTitle);  fiCard.add( fiCardTime);

		// 区切り線
		Panel fiCardDivider = new Panel();
		fiCardDivider.setBackground( new Color( 220, 215, 195));
		fiCardDivider.setBounds( 60, 270, 420, 1);
		fiCard.add( fiCardDivider);

		// 本日の予約
		Label fiCardTodayTitle = new Label( "  本日の予約", Label.LEFT);
		fiCardTodayTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		fiCardTodayTitle.setForeground( new Color( 130, 130, 130));
		fiCardTodayTitle.setBackground( fiCardBg);
		fiCardTodayTitle.setBounds( 80, 290, 160, 22);
		Label fiCardToday = new Label( "  ―", Label.LEFT);
		fiCardToday.setFont( new Font( "Noto Sans JP", Font.BOLD, 22));
		fiCardToday.setForeground( new Color( 60, 60, 60));
		fiCardToday.setBackground( fiCardBg);
		fiCardToday.setBounds( 80, 312, 160, 34);
		fiCard.add( fiCardTodayTitle); fiCard.add( fiCardToday);

		// 今後7日間の予約
		Label fiCardWeekTitle = new Label( "  今後7日間", Label.LEFT);
		fiCardWeekTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		fiCardWeekTitle.setForeground( new Color( 130, 130, 130));
		fiCardWeekTitle.setBackground( fiCardBg);
		fiCardWeekTitle.setBounds( 270, 290, 240, 22);
		Label fiCardWeek = new Label( "  ―", Label.LEFT);
		fiCardWeek.setFont( new Font( "Noto Sans JP", Font.BOLD, 22));
		fiCardWeek.setForeground( new Color( 60, 60, 60));
		fiCardWeek.setBackground( fiCardBg);
		fiCardWeek.setBounds( 270, 312, 240, 34);
		fiCard.add( fiCardWeekTitle); fiCard.add( fiCardWeek);

		// 次の空き時間帯
		Label fiCardSlotTitle = new Label( "  次の空き時間帯", Label.LEFT);
		fiCardSlotTitle.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		fiCardSlotTitle.setForeground( new Color( 130, 130, 130));
		fiCardSlotTitle.setBackground( fiCardBg);
		fiCardSlotTitle.setBounds( 80, 360, 400, 22);
		Label fiCardSlot = new Label( "  ―", Label.LEFT);
		fiCardSlot.setFont( new Font( "Noto Sans JP", Font.BOLD, 22));
		fiCardSlot.setForeground( GREEN);
		fiCardSlot.setBackground( fiCardBg);
		fiCardSlot.setBounds( 80, 382, 400, 34);
		fiCard.add( fiCardSlotTitle); fiCard.add( fiCardSlot);

		java.awt.GridBagConstraints fgbc2 = new java.awt.GridBagConstraints();
		fgbc2.anchor = java.awt.GridBagConstraints.CENTER;
		fgbc2.fill = java.awt.GridBagConstraints.NONE;
		fgbc2.weightx = 1.0; fgbc2.weighty = 1.0;
		fiRight.add( fiCard, fgbc2);

		// 教室ボタンを生成
		final Button[] fiRoomButtons = new Button[fiIds.size()];
		Font fiBtnFont = new Font( "Noto Sans JP", Font.BOLD, 13);
		for( int i = 0; i < fiIds.size(); i++) {
			final String roomId = fiIds.get( i);
			final int idx = i;
			Button b = new Button( roomId);
			b.setFont( fiBtnFont);
			b.addActionListener( ev -> {
				// 全ボタンの強調を解除（背景・文字色とも）
				for( Button bb : fiRoomButtons) if( bb != null) {
					bb.setBackground( null);
					bb.setForeground( BUTTON_FG);
				}
				// 選択ボタンを強調
				fiRoomButtons[idx].setBackground( GREEN);
				fiRoomButtons[idx].setForeground( Color.WHITE);
				// 詳細を更新
				String exp = reservationControl.getFacilityExplanation( roomId);
				// exp の形式: "<facility_name> 座席数: NN  利用可能時間: HH:MM～HH:MM"
				fiCardRoomId.setText( roomId);
				// パース
				String roomName = "";
				String seats = "―";
				String timeRange = "―";
				int idxSeats = exp.indexOf( "座席数");
				if( idxSeats > 0) {
					roomName = exp.substring( 0, idxSeats).trim();
					int idxTime = exp.indexOf( "利用可能時間");
					if( idxTime > idxSeats) {
						seats = exp.substring( idxSeats + 4, idxTime).trim().replace( ":", "").trim();
						if( seats.startsWith( "：")) seats = seats.substring( 1).trim();
						timeRange = exp.substring( idxTime + 7).trim();
						if( timeRange.startsWith( "：")) timeRange = timeRange.substring( 1).trim();
					}
				} else {
					roomName = exp;
				}
				fiCardRoomName.setText( roomName);
				fiCardSeats.setText( " " + seats);
				fiCardTime.setText( " " + timeRange);

				// 集計情報の更新
				int todayCount = reservationControl.getReservationCountToday( roomId);
				int weekCount  = reservationControl.getReservationCount7Days( roomId);
				String nextSlot = reservationControl.getNextAvailableSlot( roomId);
				fiCardToday.setText( " " + todayCount + "件");
				fiCardWeek.setText(  " " + weekCount  + "件");
				fiCardSlot.setText(  " " + nextSlot);
			});
			fiRoomButtons[i] = b;
			fiListPanel.add( b);
		}

		ScrollPane fiScroll = new ScrollPane( ScrollPane.SCROLLBARS_AS_NEEDED);
		fiScroll.add( fiListPanel);
		fiLeft.add( fiScroll, BorderLayout.CENTER);

		Panel fiBody = new Panel( new BorderLayout( 0, 0));
		fiBody.setBackground( BG);
		fiBody.add( fiLeft,  BorderLayout.WEST);
		fiBody.add( fiRight, BorderLayout.CENTER);
		cardFacilityInfo.add( fiBody, BorderLayout.CENTER);

		// 互換性のため fiChoice / fiText のフィールドは保持するが使用しない
		fiChoice = new ChoiceFacility( fiIds);
		fiText = new TextArea( "", 1, 1);

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

		panelContent.add( cardLogin,        "login");
		panelContent.add( cardStatus,       "status");
		panelContent.add( cardFacilityInfo, "facilityinfo");
		panelContent.add( cardMyRes,        "myres");
		panelContent.add( cardNewRes,       "newres");
		panelContent.add( cardMessage,      "message");
		panelContent.add( cardSettings,     "settings");

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
		btnPrevDay.addActionListener( this);
		btnFloorFilter.addActionListener( this);
		btnNextDay.addActionListener( this);
		buttonStatusView.addActionListener( this);
		buttonFacilityInfo.addActionListener( this);
		buttonMyReservation.addActionListener( this);
		buttonReservation.addActionListener( this);
		buttonSettings.addActionListener( this);
		addWindowListener( this);

		setMinimumSize( new Dimension( 1100, 700));
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
			menu.add( buttonFacilityInfo);
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
			menu.add( buttonFacilityInfo);
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

	// 確認ダイアログ（OK/キャンセル）
	private boolean showConfirmDialog( String message) {
		final Dialog dlg = new Dialog( this, "確認", true);
		final boolean[] result = { false};
		dlg.setLayout( new BorderLayout( 8, 8));
		dlg.setBackground( BG);
		dlg.setSize( 380, 180);
		dlg.setLocationRelativeTo( this);
		dlg.setResizable( false);

		TextArea msg = new TextArea( message, 3, 30,
				TextArea.SCROLLBARS_NONE);
		msg.setEditable( false); msg.setBackground( BG);
		msg.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		dlg.add( msg, BorderLayout.CENTER);

		Panel south = new Panel(); south.setBackground( BG);
		Button btnOK     = new Button( "  OK  ");
		Button btnCancel = new Button( " キャンセル ");
		btnOK.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		btnCancel.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		btnOK.setBackground( new Color( 200, 50, 50));
		btnOK.setForeground( Color.WHITE);
		btnOK.addActionListener( ev -> { result[0] = true; dlg.setVisible( false); dlg.dispose(); });
		btnCancel.addActionListener( ev -> { dlg.setVisible( false); dlg.dispose(); });
		south.add( btnCancel); south.add( btnOK);
		dlg.add( south, BorderLayout.SOUTH);

		dlg.addWindowListener( new java.awt.event.WindowAdapter() {
			@Override public void windowClosing( WindowEvent e) { dlg.setVisible( false); dlg.dispose(); }
		});
		dlg.setVisible( true);
		return result[0];
	}

	// 情報ダイアログ
	private void showInfoDialog( String message) {
		final Dialog dlg = new Dialog( this, "通知", true);
		dlg.setLayout( new BorderLayout( 8, 8));
		dlg.setBackground( BG);
		dlg.setSize( 380, 160);
		dlg.setLocationRelativeTo( this);
		dlg.setResizable( false);

		Label msg = new Label( "  " + message);
		msg.setBackground( BG);
		msg.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		dlg.add( msg, BorderLayout.CENTER);

		Panel south = new Panel(); south.setBackground( BG);
		Button btnOK = new Button( "  OK  ");
		btnOK.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		btnOK.addActionListener( ev -> { dlg.setVisible( false); dlg.dispose(); });
		south.add( btnOK);
		dlg.add( south, BorderLayout.SOUTH);

		dlg.addWindowListener( new java.awt.event.WindowAdapter() {
			@Override public void windowClosing( WindowEvent e) { dlg.setVisible( false); dlg.dispose(); }
		});
		dlg.setVisible( true);
	}

	// 自己予約一覧を再構築して表示する
	private void refreshMyReservations() {
		myResCenter.removeAll();

		if( !reservationControl.isLoggedIn()) {
			Label msg = new Label( "  ログインして下さい。");
			msg.setFont( new Font( "Noto Sans JP", Font.PLAIN, 14));
			msg.setBackground( BG);
			myResCenter.add( msg, BorderLayout.NORTH);
			myResCenter.validate();
			cardLayout.show( panelContent, "myres");
			return;
		}

		ArrayList<String[]> list = reservationControl.getMyReservationsList();

		// ソート
		list.sort( ( a, b) -> {
			int col = myResSortCol == 2 ? 0 : 1;	// 2=教室(index0), 1=日付(index1)
			int cmp = a[col].compareTo( b[col]);
			if( cmp == 0) cmp = a[1].compareTo( b[1]);	// 同じなら日付で
			return myResSortAsc ? cmp : -cmp;
		});

		Color headerBg = new Color( 220, 215, 195);
		Color rowBg    = BG;
		Color rowAlt   = new Color( 245, 238, 215);
		Color gridLine = new Color( 200, 195, 180);
		Font  hFont    = new Font( "Noto Sans JP", Font.BOLD,  13);
		Font  rFont    = new Font( "Noto Sans JP", Font.PLAIN, 13);
		String today   = new java.text.SimpleDateFormat( "yyyy-MM-dd")
				.format( java.util.Calendar.getInstance().getTime());

		// テーブルを固定幅のパネルで囲んでスリムに
		Panel tableWrapper = new Panel( new java.awt.GridBagLayout());
		tableWrapper.setBackground( BG);

		int[] colWidths = { 80, 130, 130, 170, 90, 90};
		int rowH    = 30;
		int totalW  = 0;
		int[] colX  = new int[colWidths.length];
		for( int c = 0; c < colWidths.length; c++) {
			colX[c] = totalW;
			totalW += colWidths[c] + 1;
		}
		int totalH = ( list.size() + 1) * ( rowH + 1) + 4;

		Panel table = new Panel( null);
		table.setBackground( gridLine);
		table.setPreferredSize( new Dimension( totalW, totalH));

		// --- ヘッダー行 ---
		String facArrow  = myResSortCol == 2 ? ( myResSortAsc ? " ▲" : " ▼") : "";
		String dateArrow = myResSortCol == 1 ? ( myResSortAsc ? " ▲" : " ▼") : "";

		Button hFacility = new Button( "教室" + facArrow);
		hFacility.setFont( hFont); hFacility.setBackground( headerBg);
		hFacility.setBounds( colX[0] + 1, 1, colWidths[0], rowH);
		hFacility.addActionListener( ev -> {
			if( myResSortCol == 2) myResSortAsc = !myResSortAsc;
			else { myResSortCol = 2; myResSortAsc = true; }
			refreshMyReservations();
		});

		Button hDate = new Button( "日付" + dateArrow);
		hDate.setFont( hFont); hDate.setBackground( headerBg);
		hDate.setBounds( colX[1] + 1, 1, colWidths[1], rowH);
		hDate.addActionListener( ev -> {
			if( myResSortCol == 1) myResSortAsc = !myResSortAsc;
			else { myResSortCol = 1; myResSortAsc = true; }
			refreshMyReservations();
		});

		Label hTime = new Label( " 時間（" + list.size() + "件）", Label.LEFT);
		hTime.setFont( hFont); hTime.setBackground( headerBg);
		hTime.setBounds( colX[2] + 1, 1, colWidths[2], rowH);

		Label hReservedAt = new Label( " 予約日時", Label.LEFT);
		hReservedAt.setFont( hFont); hReservedAt.setBackground( headerBg);
		hReservedAt.setBounds( colX[3] + 1, 1, colWidths[3], rowH);

		Label hEdit = new Label( " 変更", Label.CENTER);
		hEdit.setFont( hFont); hEdit.setBackground( headerBg);
		hEdit.setBounds( colX[4] + 1, 1, colWidths[4], rowH);

		Label hDel = new Label( " 削除", Label.CENTER);
		hDel.setFont( hFont); hDel.setBackground( headerBg);
		hDel.setBounds( colX[5] + 1, 1, colWidths[5], rowH);

		table.add( hFacility); table.add( hDate); table.add( hTime); table.add( hReservedAt);
		table.add( hEdit); table.add( hDel);

		// --- データ行 ---
		Font  btnFont = new Font( "Noto Sans JP", Font.BOLD, 12);
		for( int i = 0; i < list.size(); i++) {
			final String[] res = list.get( i);
			Color bg = ( i % 2 == 0) ? rowBg : rowAlt;
			int y = ( i + 1) * ( rowH + 1) + 1;

			String[] dp    = res[1].split( "-");
			String dispDay = dp[0] + "/" + dp[1] + "/" + dp[2];
			String dayNote = res[1].equals( today) ? " （本日）" : "";
			String reservedAt = ( res[4] != null) ? res[4] : "";

			Label lFac  = new Label( "  " + res[0], Label.LEFT);
			Label lDate = new Label( "  " + dispDay + dayNote, Label.LEFT);
			Label lTime = new Label( "  " + res[2].substring(0,5) + " ～ " + res[3].substring(0,5), Label.LEFT);
			Label lResAt = new Label( "  " + reservedAt, Label.LEFT);

			lFac.setBounds(   colX[0] + 1, y, colWidths[0], rowH);
			lDate.setBounds(  colX[1] + 1, y, colWidths[1], rowH);
			lTime.setBounds(  colX[2] + 1, y, colWidths[2], rowH);
			lResAt.setBounds( colX[3] + 1, y, colWidths[3], rowH);

			for( Label l : new Label[]{ lFac, lDate, lTime, lResAt}) {
				l.setFont( rFont); l.setBackground( bg);
				table.add( l);
			}

			// 本日の予約は変更・削除不可
			boolean canEdit = !res[1].equals( today);
			final int reservationId = Integer.parseInt( res[5]);

			Button bEdit = new Button( "変更");
			bEdit.setFont( btnFont);
			bEdit.setEnabled( canEdit);
			bEdit.setBounds( colX[4] + 8, y + 3, colWidths[4] - 16, rowH - 6);
			bEdit.addActionListener( ev -> {
				EditReservationDialog dlg = new EditReservationDialog(
						this, reservationControl, reservationId,
						res[0], res[1], res[2], res[3]);
				dlg.setVisible( true);
				if( dlg.updated) {
					refreshMyReservations();
				}
			});

			Button bDel = new Button( "削除");
			bDel.setFont( btnFont);
			bDel.setEnabled( canEdit);
			bDel.setBounds( colX[5] + 8, y + 3, colWidths[5] - 16, rowH - 6);
			bDel.addActionListener( ev -> {
				if( showConfirmDialog( "この予約を削除しますか？\n" + res[0] + " " + dispDay + " "
						+ res[2].substring(0,5) + "～" + res[3].substring(0,5))) {
					String result = reservationControl.deleteReservation( reservationId);
					if( !result.isEmpty()) {
						showInfoDialog( result);
					}
					refreshMyReservations();
				}
			});

			table.add( bEdit); table.add( bDel);
		}

		if( list.isEmpty()) {
			Label none = new Label( "  本日以降の予約はありません。");
			none.setFont( rFont); none.setBackground( rowBg);
			none.setBounds( 1, rowH + 2, totalW, rowH);
			table.add( none);
		}

		java.awt.GridBagConstraints wgbc = new java.awt.GridBagConstraints();
		wgbc.anchor = java.awt.GridBagConstraints.NORTH;
		wgbc.fill   = java.awt.GridBagConstraints.NONE;
		wgbc.weightx = 1.0; wgbc.weighty = 1.0;
		tableWrapper.add( table, wgbc);

		ScrollPane sp = new ScrollPane( ScrollPane.SCROLLBARS_AS_NEEDED);
		sp.add( tableWrapper);
		myResCenter.add( sp, BorderLayout.CENTER);
		myResCenter.validate();
		cardLayout.show( panelContent, "myres");
	}

	// CSVインポート結果テーブルを構築
	private void buildCsvResultTable( ArrayList<String[]> results) {
		nrCsvDetail.removeAll();

		Color headerBg = new Color( 220, 215, 195);
		Color okBg     = new Color( 200, 235, 200);
		Color ngBg     = new Color( 255, 215, 215);
		Color gridLine = new Color( 200, 195, 180);
		Font  hFont    = new Font( "Noto Sans JP", Font.BOLD, 12);
		Font  rFont    = new Font( "Noto Sans JP", Font.PLAIN, 12);

		int[] colWidths = { 50, 40, 60, 110, 120, 400};
		int rowH    = 28;
		int totalW  = 0;
		int[] colX  = new int[colWidths.length];
		for( int c = 0; c < colWidths.length; c++) {
			colX[c] = totalW;
			totalW += colWidths[c] + 1;
		}
		int totalH = ( results.size() + 1) * ( rowH + 1) + 4;

		Panel table = new Panel( null);
		table.setBackground( gridLine);
		table.setPreferredSize( new Dimension( totalW, totalH));

		String[] headers = { "結果", "行", "教室", "日付", "時間", "メッセージ"};

		// ヘッダー行
		for( int c = 0; c < headers.length; c++) {
			Label l = new Label( " " + headers[c], Label.LEFT);
			l.setFont( hFont); l.setBackground( headerBg);
			l.setBounds( colX[c] + 1, 1, colWidths[c], rowH);
			table.add( l);
		}

		// データ行
		for( int i = 0; i < results.size(); i++) {
			String[] r = results.get( i);
			Color bg = "OK".equals( r[0]) ? okBg : ngBg;
			String[] cells = { r[0], r[1], r[2], r[3], r[4] + "～" + r[5], r[6]};
			int y = ( i + 1) * ( rowH + 1) + 1;

			for( int c = 0; c < cells.length; c++) {
				Label l = new Label( " " + cells[c], Label.LEFT);
				l.setFont( rFont); l.setBackground( bg);
				l.setBounds( colX[c] + 1, y, colWidths[c], rowH);
				table.add( l);
			}
		}

		ScrollPane sp = new ScrollPane( ScrollPane.SCROLLBARS_AS_NEEDED);
		sp.add( table);
		nrCsvDetail.add( sp, BorderLayout.CENTER);
		nrCsvDetail.validate();
	}

	// TextField を数字のみ・maxLen 桁までに制限する
	private void limitTextLength( final TextField tf, final int maxLen) {
		limitTextLength( tf, maxLen, Integer.MAX_VALUE);
	}

	// TextField を数字のみ・maxLen 桁まで・最大値 maxVal までに制限する
	private void limitTextLength( final TextField tf, final int maxLen, final int maxVal) {
		tf.addTextListener( new java.awt.event.TextListener() {
			@Override
			public void textValueChanged( java.awt.event.TextEvent e) {
				String t = tf.getText();
				StringBuilder sb = new StringBuilder();
				for( char c : t.toCharArray()) {
					if( Character.isDigit( c)) sb.append( c);
				}
				String filtered = sb.toString();
				if( filtered.length() > maxLen) filtered = filtered.substring( 0, maxLen);
				// 範囲チェック：最大値を超えていたら最後の桁を切る
				while( !filtered.isEmpty()) {
					try {
						int val = Integer.parseInt( filtered);
						if( val > maxVal) {
							filtered = filtered.substring( 0, filtered.length() - 1);
						} else break;
					} catch( NumberFormatException ex) { break; }
				}
				if( !filtered.equals( t)) {
					int caret = tf.getCaretPosition();
					tf.setText( filtered);
					tf.setCaretPosition( Math.min( caret, filtered.length()));
				}
			}
		});
	}

	// 認証情報をローカルに保存
	private void saveCredentials( String userId, String password) {
		try( java.io.PrintWriter pw = new java.io.PrintWriter(
				new java.io.OutputStreamWriter(
					new java.io.FileOutputStream( CREDS_FILE), "UTF-8"))) {
			pw.println( userId);
			pw.println( password);
		} catch( Exception e) { e.printStackTrace(); }
	}

	// 認証情報を読み込み（{userId, password} または null）
	private String[] loadCredentials() {
		java.io.File f = new java.io.File( CREDS_FILE);
		if( !f.exists()) return null;
		try( java.io.BufferedReader br = new java.io.BufferedReader(
				new java.io.InputStreamReader(
					new java.io.FileInputStream( f), "UTF-8"))) {
			String userId = br.readLine();
			String pass   = br.readLine();
			if( userId != null && pass != null) return new String[]{ userId, pass};
		} catch( Exception e) { e.printStackTrace(); }
		return null;
	}

	// 保存された認証情報を削除
	private void clearCredentials() {
		java.io.File f = new java.io.File( CREDS_FILE);
		if( f.exists()) f.delete();
	}

	// テーマを適用する（BUG-ST-006修正：背景色だけでなく前景色も連動）
	private void applyTheme( int themeId) {
		int[] t = THEMES[themeId];
		Color oldGreen   = GREEN;
		Color oldSidebar = SIDEBAR;
		Color oldBG      = BG;
		Color oldButton  = BUTTON_FG;
		GREEN     = new Color( t[0], t[1], t[2]);
		SIDEBAR   = new Color( t[3], t[4], t[5]);
		BG        = new Color( t[6], t[7], t[8]);
		BUTTON_FG = new Color( t[9], t[10], t[11]);
		// 背景色の置換
		recolorBg( this, oldGreen,   GREEN);
		recolorBg( this, oldSidebar, SIDEBAR);
		recolorBg( this, oldBG,      BG);
		// 前景色の置換（プライマリ＝旧緑/旧BUTTON_FG/旧BGなど）
		recolorFg( this, oldGreen,   GREEN);
		recolorFg( this, oldButton,  BUTTON_FG);
		recolorFg( this, oldBG,      BG);
		// ヘッダラベルは前景が BG（旧）→ BG（新）
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

	// コンポーネントを再帰的に前景色を更新する
	private void recolorFg( java.awt.Component c, Color oldC, Color newC) {
		if( oldC.equals( c.getForeground())) c.setForeground( newC);
		if( c instanceof java.awt.Container) {
			for( java.awt.Component child : ((java.awt.Container) c).getComponents()) {
				recolorFg( child, oldC, newC);
			}
		}
	}

	// 階フィルタドロップダウンを表示（カレンダーと同じくタイトルバーなしのフローティング）
	private void showFloorFilterDialog() {
		ArrayList<String> facIds = reservationControl.getFacilityId();
		java.util.TreeSet<String> floors = new java.util.TreeSet<>();
		for( String fid : facIds) {
			if( fid != null && fid.length() >= 2) floors.add( fid.substring( 0, 2));
		}

		final Dialog dlg = new Dialog( this, "", false);
		dlg.setUndecorated( true);
		dlg.setLayout( new BorderLayout( 0, 0));
		dlg.setBackground( Color.BLACK);	// 外枠用

		int width = 280;
		int rowH = 36;
		int headerH = 36;
		int footerH = 52;
		int height = headerH + floors.size() * rowH + footerH + 16;
		dlg.setSize( width, height);
		dlg.setResizable( false);

		// 内側コンテンツPanel（外枠を作るためにDialogに直接追加するのは inner にする）
		Panel inner = new Panel( new BorderLayout( 0, 0));
		inner.setBackground( BG);

		// ヘッダ
		Panel header = new Panel( new BorderLayout());
		header.setBackground( GREEN);
		header.setPreferredSize( new Dimension( width, headerH));
		Label headerLabel = new Label( "  表示する階を選択", Label.LEFT);
		headerLabel.setFont( new Font( "Noto Sans JP", Font.BOLD, 14));
		headerLabel.setForeground( Color.WHITE);
		headerLabel.setBackground( GREEN);
		header.add( headerLabel, BorderLayout.CENTER);
		inner.add( header, BorderLayout.NORTH);

		// チェックボックス領域
		Panel center = new Panel( new GridLayout( 0, 1, 0, 0));
		Color cardBg = new Color( 255, 252, 242);
		Color altBg  = BG;
		center.setBackground( cardBg);
		final java.util.HashMap<String, java.awt.Checkbox> cbMap = new java.util.HashMap<>();
		int i = 0;
		for( String f : floors) {
			Panel row = new Panel( new BorderLayout());
			row.setBackground( ( i % 2 == 0) ? cardBg : altBg);
			java.awt.Checkbox cb = new java.awt.Checkbox( "  " + f + " 階", selectedFloors.contains( f));
			cb.setFont( new Font( "Noto Sans JP", Font.BOLD, 15));
			cb.setBackground( ( i % 2 == 0) ? cardBg : altBg);
			cb.setForeground( new Color( 50, 50, 50));
			cbMap.put( f, cb);
			row.add( cb, BorderLayout.CENTER);
			center.add( row);
			i++;
		}
		inner.add( center, BorderLayout.CENTER);

		// フッタ
		Panel south = new Panel( new java.awt.FlowLayout( java.awt.FlowLayout.CENTER, 6, 10));
		south.setBackground( BG);
		south.setPreferredSize( new Dimension( width, footerH));
		Button btnAll   = new Button( "全選択");
		Button btnNone  = new Button( "全解除");
		Button btnApply = new Button( "適用");
		btnAll.setFont(   new Font( "Noto Sans JP", Font.BOLD, 12));
		btnNone.setFont(  new Font( "Noto Sans JP", Font.BOLD, 12));
		btnApply.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		btnAll.setPreferredSize(   new Dimension( 70, 30));
		btnNone.setPreferredSize(  new Dimension( 70, 30));
		btnApply.setPreferredSize( new Dimension( 90, 30));
		btnApply.setBackground( GREEN);
		btnApply.setForeground( Color.WHITE);

		btnAll.addActionListener(  ev -> { for( java.awt.Checkbox cb : cbMap.values()) cb.setState( true); });
		btnNone.addActionListener( ev -> { for( java.awt.Checkbox cb : cbMap.values()) cb.setState( false); });
		btnApply.addActionListener( ev -> {
			selectedFloors.clear();
			for( java.util.Map.Entry<String, java.awt.Checkbox> entry : cbMap.entrySet()) {
				if( entry.getValue().getState()) selectedFloors.add( entry.getKey());
			}
			dlg.setVisible( false); dlg.dispose();
			refreshStatusGrid();
		});

		south.add( btnAll); south.add( btnNone); south.add( btnApply);
		inner.add( south, BorderLayout.SOUTH);

		// 中央にinnerを配置、4辺に1pxの黒スペーサーで外枠を作る
		dlg.add( inner, BorderLayout.CENTER);
		Panel topL = new Panel(); topL.setBackground( Color.BLACK); topL.setPreferredSize( new Dimension( 0, 1));
		Panel botL = new Panel(); botL.setBackground( Color.BLACK); botL.setPreferredSize( new Dimension( 0, 1));
		Panel lftL = new Panel(); lftL.setBackground( Color.BLACK); lftL.setPreferredSize( new Dimension( 1, 0));
		Panel rgtL = new Panel(); rgtL.setBackground( Color.BLACK); rgtL.setPreferredSize( new Dimension( 1, 0));
		dlg.add( topL, BorderLayout.NORTH);
		dlg.add( botL, BorderLayout.SOUTH);
		dlg.add( lftL, BorderLayout.WEST);
		dlg.add( rgtL, BorderLayout.EAST);

		// ボタン直下に表示（画面端補正あり）
		java.awt.Point p = btnFloorFilter.getLocationOnScreen();
		int x = p.x + btnFloorFilter.getWidth() - width;
		int screenW = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		if( x + width > screenW) x = screenW - width - 5;
		if( x < 0) x = 0;
		dlg.setLocation( x, p.y + btnFloorFilter.getHeight());

		dlg.addWindowListener( new java.awt.event.WindowAdapter() {
			@Override public void windowClosing( WindowEvent e) { dlg.setVisible( false); dlg.dispose(); }
		});

		// 外部クリックで閉じる：その際に現在のチェック状態を反映
		dlg.addWindowFocusListener( new java.awt.event.WindowFocusListener() {
			@Override public void windowGainedFocus( WindowEvent e) {}
			@Override public void windowLostFocus( WindowEvent e) {
				selectedFloors.clear();
				for( java.util.Map.Entry<String, java.awt.Checkbox> entry : cbMap.entrySet()) {
					if( entry.getValue().getState()) selectedFloors.add( entry.getKey());
				}
				dlg.setVisible( false); dlg.dispose();
				refreshStatusGrid();
			}
		});

		dlg.setVisible( true);
	}

	// 日付編集の確定処理
	private void commitDateInput() {
		String t = tfStatusDate.getText().trim().replace( '/', '-').replace( '.', '-');
		// 桁数別の解釈：8桁=yyyymmdd, 7桁=yyyymdd（月1桁・日2桁）, 6桁=yyyymd（月1桁・日1桁）
		if( t.matches( "\\d{8}")) {
			t = t.substring( 0, 4) + "-" + t.substring( 4, 6) + "-" + t.substring( 6, 8);
		} else if( t.matches( "\\d{7}")) {
			t = t.substring( 0, 4) + "-" + t.substring( 4, 5) + "-" + t.substring( 5, 7);
		} else if( t.matches( "\\d{6}")) {
			t = t.substring( 0, 4) + "-" + t.substring( 4, 5) + "-" + t.substring( 5, 6);
		}
		try {
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat( "yyyy-MM-dd");
			df.setLenient( false);
			String norm = df.format( df.parse( t));
			currentStatusDate = norm;
			String[] parts = norm.split( "-");
			labelStatusDate.setText( parts[0] + "年" + parts[1] + "月" + parts[2] + "日");
			refreshStatusGrid();
		} catch( Exception ex) {
			// パース失敗時はキャンセル扱い
		}
		dateCardLayout.show( dateCardPanel, "label");
	}

	// 日付編集のキャンセル処理
	private void cancelDateInput() {
		String[] dp = currentStatusDate.split( "-");
		tfStatusDate.setText( dp[0] + "/" + dp[1] + "/" + dp[2]);
		dateCardLayout.show( dateCardPanel, "label");
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
		// 階フィルタを適用（前半2桁が selectedFloors に含まれる教室のみ表示）
		if( selectedFloors != null && !selectedFloors.isEmpty()) {
			ArrayList<String> filtered = new ArrayList<>();
			for( String fid : facilities) {
				if( fid != null && fid.length() >= 2 && selectedFloors.contains( fid.substring( 0, 2))) {
					filtered.add( fid);
				}
			}
			facilities = filtered;
		}

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
						// 分単位で重なり判定（BUG-ST-004修正：09:30～10:30 のような予約も正しく表示）
						int rStartMin = Integer.parseInt( res[2].substring( 0, 2)) * 60
								+ Integer.parseInt( res[2].substring( 3, 5));
						int rEndMin   = Integer.parseInt( res[3].substring( 0, 2)) * 60
								+ Integer.parseInt( res[3].substring( 3, 5));
						int cellStart = hour * 60;
						int cellEnd   = ( hour + 1) * 60;
						if( rStartMin < cellEnd && cellStart < rEndMin) {
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
				if( cbRememberMe.getState()) {
					saveCredentials( userId, pass);
				} else {
					clearCredentials();
				}
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
			cbRememberMe.setState( false);
			clearCredentials();
			buildMenu( false);
			cardLayout.show( panelContent, "login");

		} else if( e.getSource() == btnPrevDay || e.getSource() == btnNextDay) {
			Calendar cal = Calendar.getInstance();
			try {
				cal.setTime( new SimpleDateFormat( "yyyy-MM-dd").parse( currentStatusDate));
			} catch( Exception ex) {}
			cal.add( Calendar.DAY_OF_MONTH, ( e.getSource() == btnPrevDay) ? -1 : 1);
			currentStatusDate = new SimpleDateFormat( "yyyy-MM-dd").format( cal.getTime());
			labelStatusDate.setText( new SimpleDateFormat( "yyyy年MM月dd日").format( cal.getTime()));
			refreshStatusGrid();

		} else if( e.getSource() == btnFloorFilter) {
			showFloorFilterDialog();

		} else if( e.getSource() == btnCalendar) {
			Calendar cal = Calendar.getInstance();
			CalendarDialog cd = new CalendarDialog( this,
					cal.get( Calendar.YEAR), cal.get( Calendar.MONTH));
			// 日付選択時のコールバック（modeless化に伴う変更）
			cd.onDateSelected = date -> {
				currentStatusDate = date;
				String[] parts = currentStatusDate.split( "-");
				labelStatusDate.setText( parts[0] + "年" + parts[1] + "月" + parts[2] + "日");
				refreshStatusGrid();
			};
			java.awt.Point p = btnCalendar.getLocationOnScreen();
			int calW    = 310;
			int calX    = p.x + btnCalendar.getWidth() - calW;
			int screenW = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			if( calX + calW > screenW) calX = screenW - calW - 5;
			if( calX < 0)             calX = 0;
			cd.setLocation( calX, p.y + btnCalendar.getHeight());
			cd.setVisible( true);

		} else if( e.getSource() == buttonGoToLogin) {
			cardLayout.show( panelContent, "login");

		} else if( e.getSource() == buttonFacilityInfo) {
			cardLayout.show( panelContent, "facilityinfo");

		} else if( e.getSource() == buttonStatusView) {
			currentStatusDate = new SimpleDateFormat( "yyyy-MM-dd").format( Calendar.getInstance().getTime());
			labelStatusDate.setText(
					new SimpleDateFormat( "yyyy年MM月dd日").format( Calendar.getInstance().getTime()) + "  ");
			refreshStatusGrid();

		} else if( e.getSource() == buttonMyReservation) {
			refreshMyReservations();

		} else if( e.getSource() == buttonReservation) {
			// 新規予約ページに切り替え
			nrYear.setText( ""); nrMonth.setText( ""); nrDay.setText( "");
			nrResult.setText( ""); nrCsvResult.setText( ""); nrCsvPath.setText( "");
			nrTabLayout.show( nrTabContent, "manual");
			cardLayout.show( panelContent, "newres");

		} else if( e.getSource() == nrBtnManual) {
			nrTabLayout.show( nrTabContent, "manual");

		} else if( e.getSource() == nrBtnCsv) {
			nrTabLayout.show( nrTabContent, "csv");

		} else if( e.getActionCommand().equals( "予約する")) {
			result = reservationControl.reserveDirect(
					nrFacility.getSelectedItem(),
					nrYear.getText().trim(), nrMonth.getText().trim(), nrDay.getText().trim(),
					nrStartHour.getSelectedItem(), nrStartMin.getSelectedItem(),
					nrEndHour.getSelectedItem(),   nrEndMin.getSelectedItem());
			nrResult.setText( "  " + result);
			nrResult.setForeground( result.contains( "予約しました")
					? new Color( 50, 130, 50) : new Color( 200, 50, 50));

		} else if( e.getActionCommand().equals( "テンプレートをダウンロードに保存")) {
			String userHome = System.getProperty( "user.home");
			String[] candidates = {
				userHome + "\\Downloads\\予約テンプレート.csv",
				userHome + "\\OneDrive\\Downloads\\予約テンプレート.csv"
			};
			String savedPath = null;
			for( String path : candidates) {
				java.io.File parent = new java.io.File( path).getParentFile();
				if( parent != null && parent.exists()) {
					try( java.io.FileOutputStream fos = new java.io.FileOutputStream( path)) {
						fos.write( new byte[]{ (byte)0xEF, (byte)0xBB, (byte)0xBF}); // UTF-8 BOM
						java.io.PrintWriter pw = new java.io.PrintWriter(
								new java.io.OutputStreamWriter( fos, "UTF-8"));
						pw.println( "facility_id,day,start,end");
						pw.println( "251,20260610,09:00,11:00");
						pw.flush();
						savedPath = path;
						break;
					} catch( Exception ex) { ex.printStackTrace(); }
				}
			}
			if( savedPath != null) {
				nrCsvResult.setText( "  保存しました: " + savedPath);
				nrCsvResult.setForeground( new Color( 50, 130, 50));
			} else {
				nrCsvResult.setText( "  ダウンロードフォルダが見つかりません。");
				nrCsvResult.setForeground( new Color( 200, 50, 50));
			}

		} else if( e.getActionCommand().equals( "参照...")) {
			java.awt.FileDialog fd = new java.awt.FileDialog( this, "CSVファイルを選択", java.awt.FileDialog.LOAD);
			fd.setFile( "*.csv");
			fd.setVisible( true);
			if( fd.getFile() != null) {
				nrCsvPath.setText( fd.getDirectory() + fd.getFile());
			}

		} else if( e.getActionCommand().equals( "インポート")) {
			String path = nrCsvPath.getText().trim();
			if( path.isEmpty()) {
				nrCsvResult.setText( "  CSVファイルを指定してください。");
				nrCsvResult.setForeground( new Color( 200, 50, 50));
				nrCsvDetail.removeAll();
				nrCsvDetail.validate();
			} else {
				ArrayList<String[]> results = reservationControl.importCSVDetailed( path);
				int ok = 0, ng = 0;
				for( String[] r : results) {
					if( "OK".equals( r[0])) ok++; else ng++;
				}
				nrCsvResult.setText( "  完了: 成功 " + ok + "件 / 失敗 " + ng + "件");
				nrCsvResult.setForeground( ng == 0 ? new Color( 50, 130, 50) : new Color( 200, 50, 50));
				buildCsvResultTable( results);
			}

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
		// 保存された認証情報があれば自動入力
		String[] creds = loadCredentials();
		if( creds != null) {
			tfSidebarUserID.setText(   creds[0]);
			tfSidebarPassword.setText( creds[1]);
			cbRememberMe.setState( true);
		}
		cardLayout.show( panelContent, "login");
	}
	@Override public void windowClosed( WindowEvent e)      {}
	@Override public void windowIconified( WindowEvent e)   {}
	@Override public void windowDeiconified( WindowEvent e) {}
	@Override public void windowActivated( WindowEvent e)   {}
	@Override public void windowDeactivated( WindowEvent e) {}
}
