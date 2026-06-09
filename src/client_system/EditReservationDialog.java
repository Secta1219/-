package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class EditReservationDialog extends Dialog implements ActionListener {

	boolean		updated = false;
	int			reservationId;
	ReservationControl	rc;

	ChoiceFacility	cfFacility;
	TextField		tfYear, tfMonth, tfDay;
	ChoiceHour		startHour, endHour;
	ChoiceMinute	startMin,  endMin;
	Button			btnUpdate, btnCancel;
	Label			labelResult;

	static final Color BG    = new Color( 250, 242, 220);
	static final Color GREEN = new Color(  67, 160,  71);

	public EditReservationDialog( Frame owner, ReservationControl rc, int reservationId,
			String facilityId, String day, String startTime, String endTime) {
		super( owner, "予約を変更", true);
		this.rc            = rc;
		this.reservationId = reservationId;
		setBackground( BG);
		setLayout( null);
		setSize( 460, 360);
		setResizable( false);
		setLocationRelativeTo( owner);

		Font tf  = new Font( "Noto Sans JP", Font.BOLD,  17);
		Font lf  = new Font( "Noto Sans JP", Font.BOLD,  13);
		Font inf = new Font( "Noto Sans JP", Font.PLAIN, 14);

		Label title = new Label( "  予約変更");
		title.setFont( tf); title.setForeground( GREEN); title.setBackground( BG);
		title.setBounds( 20, 30, 300, 28);
		add( title);

		List<String> facIds = new ArrayList<String>();
		facIds = rc.getFacilityId();
		cfFacility = new ChoiceFacility( facIds);
		cfFacility.setFont( inf);
		cfFacility.select( facilityId);
		cfFacility.setBounds( 110, 70, 100, 26);

		Label lFac = new Label( "教室"); lFac.setFont( lf); lFac.setBackground( BG);
		lFac.setBounds( 20, 70, 80, 26);
		add( lFac); add( cfFacility);

		Label lDay = new Label( "予約日"); lDay.setFont( lf); lDay.setBackground( BG);
		lDay.setBounds( 20, 110, 80, 26);
		String[] dp = day.split( "-");
		tfYear  = new TextField( dp[0], 4); tfYear.setFont( inf);  tfYear.setBackground( Color.WHITE);
		tfMonth = new TextField( dp[1], 2); tfMonth.setFont( inf); tfMonth.setBackground( Color.WHITE);
		tfDay   = new TextField( dp[2], 2); tfDay.setFont( inf);   tfDay.setBackground( Color.WHITE);
		tfYear.setBounds(  110, 110, 60, 26); Label ly = new Label("年"); ly.setFont(lf); ly.setBackground(BG); ly.setBounds( 174, 110, 20, 26);
		tfMonth.setBounds( 200, 110, 40, 26); Label lm = new Label("月"); lm.setFont(lf); lm.setBackground(BG); lm.setBounds( 244, 110, 20, 26);
		tfDay.setBounds(   270, 110, 40, 26); Label ld = new Label("日"); ld.setFont(lf); ld.setBackground(BG); ld.setBounds( 314, 110, 20, 26);
		add( tfYear); add( ly); add( tfMonth); add( lm); add( tfDay); add( ld);

		String sh = startTime.substring( 0, 2);
		String sm = startTime.substring( 3, 5);
		String eh = endTime.substring(   0, 2);
		String em = endTime.substring(   3, 5);

		// 教室の利用可能時間で初期化
		int[][] limit = rc.getAvailableTime( facilityId);
		startHour = new ChoiceHour(); startHour.resetRange( limit[0][0], limit[1][0]);
		endHour   = new ChoiceHour(); endHour.resetRange(   limit[0][0], limit[1][0]);
		startMin  = new ChoiceMinute(); endMin = new ChoiceMinute();
		try { startHour.select( sh); } catch( Exception ex) {}
		try { endHour.select(   eh); } catch( Exception ex) {}
		try { startMin.select(  sm); } catch( Exception ex) {}
		try { endMin.select(    em); } catch( Exception ex) {}

		Label lSt = new Label( "開始"); lSt.setFont( lf); lSt.setBackground( BG); lSt.setBounds( 20, 150, 80, 26);
		startHour.setBounds( 110, 150, 70, 26); Label lsh = new Label("時"); lsh.setFont(lf); lsh.setBackground(BG); lsh.setBounds( 184, 150, 20, 26);
		startMin.setBounds(  210, 150, 70, 26); Label lsm = new Label("分"); lsm.setFont(lf); lsm.setBackground(BG); lsm.setBounds( 284, 150, 20, 26);
		add( lSt); add( startHour); add( lsh); add( startMin); add( lsm);

		Label lEt = new Label( "終了"); lEt.setFont( lf); lEt.setBackground( BG); lEt.setBounds( 20, 190, 80, 26);
		endHour.setBounds(   110, 190, 70, 26); Label leh = new Label("時"); leh.setFont(lf); leh.setBackground(BG); leh.setBounds( 184, 190, 20, 26);
		endMin.setBounds(    210, 190, 70, 26); Label lem = new Label("分"); lem.setFont(lf); lem.setBackground(BG); lem.setBounds( 284, 190, 20, 26);
		add( lEt); add( endHour); add( leh); add( endMin); add( lem);

		btnUpdate = new Button( "予約を更新");
		btnUpdate.setFont( new Font( "Noto Sans JP", Font.BOLD, 14));
		btnUpdate.setBackground( GREEN); btnUpdate.setForeground( Color.WHITE);
		btnUpdate.setBounds( 20, 240, 160, 36);
		btnCancel = new Button( "キャンセル");
		btnCancel.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		btnCancel.setBounds( 200, 240, 120, 36);
		add( btnUpdate); add( btnCancel);

		labelResult = new Label( "");
		labelResult.setFont( lf); labelResult.setBackground( BG); labelResult.setForeground( new Color( 200, 50, 50));
		labelResult.setBounds( 20, 285, 420, 22);
		add( labelResult);

		btnUpdate.addActionListener( this);
		btnCancel.addActionListener( this);

		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing( WindowEvent e) {
				setVisible( false); dispose();
			}
		});
	}

	@Override
	public void actionPerformed( ActionEvent e) {
		if( e.getSource() == btnCancel) {
			updated = false;
			setVisible( false); dispose();
		} else if( e.getSource() == btnUpdate) {
			String res = rc.updateReservation(
					reservationId,
					cfFacility.getSelectedItem(),
					tfYear.getText().trim(), tfMonth.getText().trim(), tfDay.getText().trim(),
					startHour.getSelectedItem(), startMin.getSelectedItem(),
					endHour.getSelectedItem(),   endMin.getSelectedItem());
			if( res.isEmpty()) {
				updated = true;
				setVisible( false); dispose();
			} else {
				labelResult.setText( "  " + res);
			}
		}
	}
}
