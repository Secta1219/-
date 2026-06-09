package client_system;

public class ReservationSystem {
	public	static	void	main( String argv[]) {
		ReservationControl	reservationControl	= new ReservationControl();				// ReservationControlクラスのインスタンス生成
		MainFrame			mainFrame			= new MainFrame( reservationControl);	// MainFrameクラスのインスタンス生成
		mainFrame.setExtendedState( java.awt.Frame.MAXIMIZED_BOTH);	// 最大化起動
		mainFrame.setVisible( true);
	}
}
