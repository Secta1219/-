package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.util.Calendar;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CalendarDialog extends Dialog implements ActionListener {

	String	selectedDate;	// 選択された日付 "yyyy-MM-dd"、キャンセル時は null

	int		year, month;

	Label	labelYearMonth;
	Panel	panelGrid;
	Button	btnPrev, btnNext, btnCancel;

	static final Color BG      = new Color( 250, 242, 220);
	static final Color GREEN   = new Color(  67, 160,  71);
	static final Color HEADER  = new Color( 220, 215, 195);
	static final Color GRIDLINE = new Color( 190, 195, 185);

	CalendarDialog( Frame owner, int initYear, int initMonth) {
		super( owner, "日付を選択", true);
		year  = initYear;
		month = initMonth;
		selectedDate = null;

		setLayout( new BorderLayout( 4, 4));
		setBackground( BG);

		// ナビゲーションヘッダー
		Panel navPanel = new Panel( new BorderLayout());
		navPanel.setBackground( GREEN);

		btnPrev = new Button( "　＜　");
		btnNext = new Button( "　＞　");
		btnPrev.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));
		btnNext.setFont( new Font( "Noto Sans JP", Font.BOLD, 13));

		labelYearMonth = new Label( "", Label.CENTER);
		labelYearMonth.setFont( new Font( "Noto Sans JP", Font.BOLD, 15));
		labelYearMonth.setForeground( Color.WHITE);
		labelYearMonth.setBackground( GREEN);

		navPanel.add( btnPrev,        BorderLayout.WEST);
		navPanel.add( labelYearMonth, BorderLayout.CENTER);
		navPanel.add( btnNext,        BorderLayout.EAST);
		add( navPanel, BorderLayout.NORTH);

		panelGrid = new Panel();
		add( panelGrid, BorderLayout.CENTER);

		Panel southPanel = new Panel();
		southPanel.setBackground( BG);
		btnCancel = new Button( "キャンセル");
		btnCancel.setFont( new Font( "Noto Sans JP", Font.BOLD, 12));
		southPanel.add( btnCancel);
		add( southPanel, BorderLayout.SOUTH);

		btnPrev.addActionListener( this);
		btnNext.addActionListener( this);
		btnCancel.addActionListener( this);

		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing( WindowEvent e) {
				setVisible( false);
				dispose();
			}
		});

		updateGrid();
		setUndecorated( true);		// タイトルバーを非表示
		setSize( 310, 270);
		setResizable( false);
	}

	private void updateGrid() {
		panelGrid.removeAll();
		panelGrid.setLayout( new GridLayout( 7, 7, 2, 2));
		panelGrid.setBackground( GRIDLINE);

		labelYearMonth.setText( String.format( "  %d年 %02d月  ", year, month + 1));

		// 曜日ヘッダー
		String[] dayNames = { "日", "月", "火", "水", "木", "金", "土" };
		Color[]  dayColors = {
			new Color( 200, 80, 80),	// 日
			new Color(  60, 60, 60),	// 月
			new Color(  60, 60, 60),	// 火
			new Color(  60, 60, 60),	// 水
			new Color(  60, 60, 60),	// 木
			new Color(  60, 60, 60),	// 金
			new Color(  80, 80, 200)	// 土
		};
		for( int i = 0; i < 7; i++) {
			Label l = new Label( dayNames[i], Label.CENTER);
			l.setBackground( HEADER);
			l.setForeground( dayColors[i]);
			l.setFont( new Font( "Noto Sans JP", Font.BOLD, 12));
			panelGrid.add( l);
		}

		// カレンダー計算
		Calendar cal = Calendar.getInstance();
		cal.set( year, month, 1);
		int firstDow    = cal.get( java.util.Calendar.DAY_OF_WEEK) - 1;	// 0=日
		int daysInMonth = cal.getActualMaximum( java.util.Calendar.DAY_OF_MONTH);

		// 今日の日付
		Calendar today = Calendar.getInstance();
		int todayYear  = today.get( java.util.Calendar.YEAR);
		int todayMonth = today.get( java.util.Calendar.MONTH);
		int todayDay   = today.get( java.util.Calendar.DAY_OF_MONTH);

		// 月頭の空白
		for( int i = 0; i < firstDow; i++) {
			Label empty = new Label( "");
			empty.setBackground( BG);
			panelGrid.add( empty);
		}

		// 日付ボタン
		for( int day = 1; day <= daysInMonth; day++) {
			final int d = day;
			Button btn = new Button( String.valueOf( day));
			btn.setFont( new Font( "Noto Sans JP", Font.PLAIN, 12));

			int dow = ( firstDow + day - 1) % 7;
			if( year == todayYear && month == todayMonth && day == todayDay) {
				btn.setBackground( GREEN);
				btn.setForeground( Color.WHITE);
			} else {
				btn.setBackground( BG);
				btn.setForeground( dayColors[dow]);
			}

			btn.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e) {
					selectedDate = String.format( "%04d-%02d-%02d", year, month + 1, d);
					setVisible( false);
					dispose();
				}
			});
			panelGrid.add( btn);
		}

		// 残りの空白（6行 × 7列 = 42セル合わせ）
		int filled = firstDow + daysInMonth;
		int remain = 42 - filled;
		for( int i = 0; i < remain; i++) {
			Label empty = new Label( "");
			empty.setBackground( BG);
			panelGrid.add( empty);
		}

		panelGrid.validate();
		panelGrid.repaint();
	}

	@Override
	public void actionPerformed( ActionEvent e) {
		if( e.getSource() == btnPrev) {
			month--;
			if( month < 0) { month = 11; year--; }
			updateGrid();
		} else if( e.getSource() == btnNext) {
			month++;
			if( month > 11) { month = 0; year++; }
			updateGrid();
		} else if( e.getSource() == btnCancel) {
			setVisible( false);
			dispose();
		}
	}
}
