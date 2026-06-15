package client_system;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ScreenshotMain {

	static String DIR;
	static Robot robot;
	static MainFrame frame;
	static int idx = 1;

	public static void main( String[] argv) throws Exception {
		DIR = System.getProperty( "user.home") + "/OneDrive/画像/Screenshots/auto/";
		new File( DIR).mkdirs();
		robot = new Robot();

		// 自動入力されないように認証情報を削除
		File creds = new File( System.getProperty( "user.home") + "/.reservation_credentials.txt");
		if( creds.exists()) creds.delete();

		ReservationControl rc = new ReservationControl();
		frame = new MainFrame( rc);
		frame.setExtendedState( Frame.MAXIMIZED_BOTH);
		frame.setVisible( true);
		Thread.sleep( 2500);
		Rectangle b = frame.getBounds();

		// 1. ログイン画面（初期表示）
		capture( b, "login_initial");

		// 2. 予約状況確認（未ログイン）
		click( frame.buttonStatusView);
		Thread.sleep( 1500);
		capture( b, "status_unlogged");

		// 3. 教室概要（未ログイン）— 1つ教室を選択した状態
		click( frame.buttonFacilityInfo);
		Thread.sleep( 1000);
		// 中央の教室ボタンを擬似クリック（左サイドのボタンを総当りで探さず、最初の Choice 経由でも良いが、ここでは選択は割愛）
		capture( b, "facility_info_unlogged_initial");

		// ログイン画面に戻る
		click( frame.buttonGoToLogin);
		Thread.sleep( 1000);

		// ログイン処理
		frame.tfSidebarUserID.setText( "TK190000");
		frame.tfSidebarPassword.setText( "pass0000");
		Thread.sleep( 500);
		click( frame.buttonSidebarLogin);
		Thread.sleep( 2500);

		// 4. 予約状況確認（ログイン後）
		click( frame.buttonStatusView);
		Thread.sleep( 1500);
		capture( b, "status_logged_in");

		// 5. 教室概要（ログイン後）
		click( frame.buttonFacilityInfo);
		Thread.sleep( 1500);
		capture( b, "facility_info_logged_in");

		// 6. 自己予約確認
		click( frame.buttonMyReservation);
		Thread.sleep( 2000);
		capture( b, "my_reservation");

		// 7. 新規予約（手動入力）
		click( frame.buttonReservation);
		Thread.sleep( 1000);
		capture( b, "new_reservation_manual");

		// 8. 新規予約（CSV一括）
		click( frame.nrBtnCsv);
		Thread.sleep( 1000);
		capture( b, "new_reservation_csv");

		// 9. 設定（パスワード変更＋テーマ）
		click( frame.buttonSettings);
		Thread.sleep( 1000);
		capture( b, "settings");

		System.out.println( "全スクショ取得完了: " + DIR);
		System.exit( 0);
	}

	static void capture( Rectangle b, String name) throws Exception {
		BufferedImage img = robot.createScreenCapture( b);
		String filename = String.format( "%02d_%s.png", idx++, name);
		ImageIO.write( img, "png", new File( DIR + filename));
		System.out.println( "保存: " + filename);
	}

	static void click( Button b) {
		b.dispatchEvent( new ActionEvent( b, ActionEvent.ACTION_PERFORMED, ""));
	}
}
