package client_system;

import java.awt.Dialog;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReservationControl {
	// MySQLに接続するためのデータ
	Connection	sqlCon;
	Statement	sqlStmt;
	String		sqlUserID	= "puser";
	String		sqlPassword	= "1234";
	// 予約システムのユーザID及びLogin状態
	String		reservationUserID;
	private	boolean	flagLogin;

	ReservationControl(){
		flagLogin = false;
	}

	public boolean isLoggedIn() {
		return flagLogin;
	}

	// MySQLに接続するためのメソッド
	private	void	connectDB() {
		try {
			Class.forName( "org.gjt.mm.mysql.Driver");
			String	url = "jdbc:mysql://localhost?useUnicode=true&characterEncoding=SJIS";
			sqlCon	= DriverManager.getConnection( url, sqlUserID, sqlPassword);
			sqlStmt	= sqlCon.createStatement();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	// MySQLから切断するためのメソッド
	private	void	closeDB() {
		try {
			if( sqlStmt != null) sqlStmt.close();
			if( sqlCon  != null) sqlCon.close();
		} catch( Exception e) {
			e.printStackTrace();
		}
	}

	//// サイドバーのログインフォームから直接認証するメソッド
	public	String	loginWithCredentials( String userId, String password, MainFrame frame) {
		String	res = "";
		connectDB();
		try {
			String	sql = "SELECT * FROM db_reservation.user WHERE user_id = ?";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, userId);
			ResultSet	rs = ps.executeQuery();
			if( rs.next()) {
				String	pw = rs.getString( "password");
				if( pw.equals( password)) {
					flagLogin          = true;
					reservationUserID  = userId;
					frame.tfLoginID.setText( userId);
					frame.buttonLog.setLabel( "ログアウト");
				} else {
					res = "IDまたはパスワードが違います。";
				}
			} else {
				res = "IDが違います。";
			}
			ps.close();
		} catch( Exception e) {
			e.printStackTrace();
			res = "予期しないエラーが発生しました。";
		}
		closeDB();
		return	res;
	}

	//// ログイン・ログアウトボタンの処理
	public	String	loginLogout( MainFrame frame) {
		String	res = "";
		if( flagLogin) {
			flagLogin	= false;
			frame.buttonLog.setLabel( " ログイン ");
			frame.tfLoginID.setText( "未ログイン");
		} else {
			LoginDialog	ld	= new LoginDialog(frame);
			ld.setBounds( 100, 100, 350, 150);
			ld.setResizable( false);
			ld.setVisible( true);
			ld.setModalityType( Dialog.ModalityType.APPLICATION_MODAL);

			if( ld.canceled) return "";

			reservationUserID	= ld.tfUserID.getText();
			String	password	= ld.tfPassword.getText();

			connectDB();
			try {
				String	sql = "SELECT * FROM db_reservation.user WHERE user_id = ?";
				PreparedStatement	ps = sqlCon.prepareStatement( sql);
				ps.setString( 1, reservationUserID);
				ResultSet	rs = ps.executeQuery();
				if( rs.next()) {
					String	pwDB = rs.getString( "password");
					if( pwDB.equals( password)) {
						flagLogin	= true;
						frame.buttonLog.setLabel( "ログアウト");
						frame.tfLoginID.setText( reservationUserID);
					} else {
						res = "IDまたはパスワードが違います。";
					}
				} else {
					res = "IDが違います。";
				}
				ps.close();
			} catch( Exception e) {
				e.printStackTrace();
			}
			closeDB();
		}
		return	res;
	}

	//// 教室概要ボタン押下時の処理を行うメソッド
	public	String	getFacilityExplanation( String facility_id) {
		String		res = "";
		connectDB();
		try {
			String	sql = "SELECT * FROM db_reservation.facility WHERE facility_id = ?";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, facility_id);
			ResultSet	rs = ps.executeQuery();
			if( rs.next()) {
				String	exp       = rs.getString( "explanation");
				String	openTime  = rs.getString( "open_time");
				String	closeTime = rs.getString( "close_time");
				res = exp + "　利用可能時間：" + openTime.substring( 0,5) + "～" + closeTime.substring( 0,5);
			} else {
				res = "教室番号が違います。";
			}
			ps.close();
		} catch( Exception e) {
			e.printStackTrace();
		}
		closeDB();
		return	res;
	}

	//// 全てのfacility_idを取得するメソッド
	public	ArrayList<String>	getFacilityId() {
		ArrayList<String> facilityId	= new ArrayList<String>();
		connectDB();
		try {
			String		sql = "SELECT * FROM db_reservation.facility ORDER BY facility_id DESC";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ResultSet	rs	= ps.executeQuery();
			while( rs.next()) {
				facilityId.add( rs.getString( "facility_id"));
			}
			ps.close();
		} catch( Exception e) {
			e.printStackTrace();
		}
		closeDB();
		return	facilityId;
	}

	//// 予約を削除するメソッド（自分の予約のみ）
	public	String	deleteReservation( int reservationId) {
		if( !flagLogin) return "ログインして下さい。";
		String res = "";
		connectDB();
		try {
			String	today = new SimpleDateFormat( "yyyy-MM-dd").format( Calendar.getInstance().getTime());
			String	sql = "DELETE FROM db_reservation.reservation"
					+ " WHERE reservation_id = ? AND user_id = ? AND day > ?";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setInt(    1, reservationId);
			ps.setString( 2, reservationUserID);
			ps.setString( 3, today);
			int affected = ps.executeUpdate();
			ps.close();
			res = affected > 0 ? "" : "削除できませんでした。（翌日以降のみ削除可能）";
		} catch( Exception e) {
			e.printStackTrace();
			res = "予期しないエラーが発生しました。";
		}
		closeDB();
		return res;
	}

	//// 予約を更新するメソッド（自分の予約のみ）
	public	String	updateReservation( int reservationId, String facility, String year, String month, String day,
			String startHour, String startMin, String endHour, String endMin) {
		if( !flagLogin) return "ログインして下さい。";
		if( month.length() == 1) month = "0" + month;
		if( day.length()   == 1) day   = "0" + day;
		try {
			DateFormat	df = new SimpleDateFormat( "yyyy-MM-dd");
			df.setLenient( false);
			String	inData = year + "-" + month + "-" + day;
			if( !inData.equals( df.format( df.parse( inData)))) return "日付の書式を修正して下さい。";
		} catch( ParseException p) { return "日付の値を修正して下さい。"; }

		Calendar	dateRes = Calendar.getInstance();
		dateRes.set( Integer.parseInt( year), Integer.parseInt( month) - 1, Integer.parseInt( day));
		if( !dateRes.after( Calendar.getInstance())) return "予約日が無効です。（翌日以降を指定）";

		// 時・分は必ず2桁に揃える（"9" → "09"）。文字列比較で "9" > "11" と誤判定されるのを防ぐ
		if( startHour.length() == 1) startHour = "0" + startHour;
		if( startMin.length()  == 1) startMin  = "0" + startMin;
		if( endHour.length()   == 1) endHour   = "0" + endHour;
		if( endMin.length()    == 1) endMin    = "0" + endMin;
		String	st = startHour + ":" + startMin + ":00";
		String	et = endHour   + ":" + endMin   + ":00";
		if( st.compareTo( et) >= 0) return "開始時刻と終了時刻が同じか終了時刻の方が早くなっています。";

		int[][]	limit = getAvailableTime( facility);
		String	sl = String.format( "%02d:%02d:00", limit[0][0], limit[0][1]);
		String	el = String.format( "%02d:%02d:00", limit[1][0], limit[1][1]);
		if( sl.compareTo( st) > 0 || el.compareTo( et) < 0) return "利用可能時間外です。";

		String res = "";
		connectDB();
		try {
			String	rdate = year + "-" + month + "-" + day;
			// 自分の編集対象以外の予約と重ならないかチェック（境界一致は重複としない＝BUG-ST-002修正）
			String	sql = "SELECT start_time, end_time FROM db_reservation.reservation"
					+ " WHERE facility_id = ? AND day = ? AND reservation_id != ?";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, facility);
			ps.setString( 2, rdate);
			ps.setInt(    3, reservationId);
			ResultSet	rs = ps.executeQuery();
			boolean overlap = false;
			while( rs.next()) {
				String	s = rs.getString( "start_time"), e = rs.getString( "end_time");
				// 端点が等しいだけ（隣接）は重複としないため厳密不等号
				if( s.compareTo( et) < 0 && st.compareTo( e) < 0) {
					overlap = true;
					break;
				}
			}
			ps.close();
			if( overlap) { closeDB(); return "既にある予約に重なっています。"; }

			sql = "UPDATE db_reservation.reservation SET"
					+ " facility_id = ?, day = ?, start_time = ?, end_time = ?"
					+ " WHERE reservation_id = ? AND user_id = ?";
			ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, facility);
			ps.setString( 2, rdate);
			ps.setString( 3, st);
			ps.setString( 4, et);
			ps.setInt(    5, reservationId);
			ps.setString( 6, reservationUserID);
			int affected = ps.executeUpdate();
			ps.close();
			res = affected > 0 ? "" : "更新できませんでした。";
		} catch( Exception e) {
			e.printStackTrace();
			res = "予期しないエラーが発生しました。";
		}
		closeDB();
		return res;
	}

	//// 直接パラメータで予約を登録するメソッド（新規予約ページ・CSVインポート共用）
	public	String	reserveDirect( String facilityId, String year, String month, String day,
			String startHour, String startMin, String endHour, String endMin) {
		if( !flagLogin) return "ログインして下さい。";
		if( month.length() == 1) month = "0" + month;
		if( day.length()   == 1) day   = "0" + day;
		try {
			DateFormat	df = new SimpleDateFormat( "yyyy-MM-dd");
			df.setLenient( false);
			String	inData = year + "-" + month + "-" + day;
			if( !inData.equals( df.format( df.parse( inData)))) return "日付の書式を修正して下さい。";
		} catch( ParseException p) { return "日付の値を修正して下さい。"; }

		Calendar	dateRes = Calendar.getInstance();
		dateRes.set( Integer.parseInt( year), Integer.parseInt( month) - 1, Integer.parseInt( day));
		if( !dateRes.after( Calendar.getInstance())) return "予約日が無効です。（翌日以降を指定）";

		// 時・分は必ず2桁に揃える（"9" → "09"）。文字列比較で "9" > "11" と誤判定されるのを防ぐ
		if( startHour.length() == 1) startHour = "0" + startHour;
		if( startMin.length()  == 1) startMin  = "0" + startMin;
		if( endHour.length()   == 1) endHour   = "0" + endHour;
		if( endMin.length()    == 1) endMin    = "0" + endMin;
		String	st = startHour + ":" + startMin + ":00";
		String	et = endHour   + ":" + endMin   + ":00";
		if( st.compareTo( et) >= 0) return "開始時刻と終了時刻が同じか終了時刻の方が早くなっています。";

		int[][]	limit = getAvailableTime( facilityId);
		String	sl = String.format( "%02d:%02d:00", limit[0][0], limit[0][1]);
		String	el = String.format( "%02d:%02d:00", limit[1][0], limit[1][1]);
		if( sl.compareTo( st) > 0 || el.compareTo( et) < 0) return "利用可能時間外です。";

		String res;
		connectDB();
		try {
			String	rdate = year + "-" + month + "-" + day;
			String	sql = "SELECT start_time, end_time FROM db_reservation.reservation"
					+ " WHERE facility_id = ? AND day = ?";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, facilityId);
			ps.setString( 2, rdate);
			ResultSet	rs = ps.executeQuery();
			boolean overlap = false;
			while( rs.next()) {
				String	s = rs.getString( "start_time"), e = rs.getString( "end_time");
				// 端点一致（隣接）は重複としない＝BUG-ST-002修正
				if( s.compareTo( et) < 0 && st.compareTo( e) < 0) {
					overlap = true;
					break;
				}
			}
			ps.close();
			if( overlap) { closeDB(); return "既にある予約に重なっています。"; }

			SimpleDateFormat	sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
			String	now = sdf.format( Calendar.getInstance().getTime());
			sql = "INSERT INTO db_reservation.reservation"
					+ " ( facility_id, user_id, date, day, start_time, end_time)"
					+ " VALUES( ?, ?, ?, ?, ?, ?)";
			ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, facilityId);
			ps.setString( 2, reservationUserID);
			ps.setString( 3, now);
			ps.setString( 4, rdate);
			ps.setString( 5, st);
			ps.setString( 6, et);
			ps.executeUpdate();
			ps.close();
			res = rdate + " " + st.substring( 0, 5) + "～" + et.substring( 0, 5) + " " + facilityId + "教室を予約しました。";
		} catch( Exception e) {
			e.printStackTrace();
			res = "予期しないエラーが発生しました。";
		}
		closeDB();
		return res;
	}

	//// CSVファイルを読み込んで一括予約するメソッド
	public	String	importCSV( String filePath) {
		if( !flagLogin) return "ログインして下さい。";
		int ok = 0, ng = 0;
		StringBuilder	log = new StringBuilder();
		try {
			java.io.BufferedReader	br = new java.io.BufferedReader( new java.io.FileReader( filePath));
			String	line;
			int		lineNum = 0;
			while(( line = br.readLine()) != null) {
				lineNum++;
				if( lineNum == 1 || line.trim().isEmpty() || line.startsWith( "#")) continue;
				String[]	cols = line.split( ",");
				if( cols.length < 4) { log.append( "行" + lineNum + ": 列数不足\n"); ng++; continue; }
				String	fac  = cols[0].trim();
				String	day  = cols[1].trim().replace( '/', '-');
				if( day.matches( "\\d{8}")) {
					day = day.substring( 0, 4) + "-" + day.substring( 4, 6) + "-" + day.substring( 6, 8);
				}
				String	st   = cols[2].trim();
				String	et   = cols[3].trim();
				String[]	dp = day.split( "-");
				String[]	sp = st.split( ":");
				String[]	ep = et.split( ":");
				if( dp.length < 3 || sp.length < 2 || ep.length < 2) {
					log.append( "行" + lineNum + ": 形式エラー\n"); ng++; continue;
				}
				String	res = reserveDirect( fac, dp[0], dp[1], dp[2], sp[0], sp[1], ep[0], ep[1]);
				if( res.contains( "予約しました")) { ok++; }
				else { log.append( "行" + lineNum + ": " + res + "\n"); ng++; }
			}
			br.close();
		} catch( Exception e) {
			return "ファイル読み込みエラー: " + e.getMessage();
		}
		return "完了: 成功 " + ok + "件 / 失敗 " + ng + "件\n" + log.toString();
	}

	//// CSVを読んで詳細結果を返す（各行: {status, line, facility, day, start, end, reason}）
	public	ArrayList<String[]>	importCSVDetailed( String filePath) {
		ArrayList<String[]>	results = new ArrayList<>();
		if( !flagLogin) {
			results.add( new String[]{ "NG", "-", "-", "-", "-", "-", "ログインして下さい。"});
			return	results;
		}
		try {
			java.io.BufferedReader	br = new java.io.BufferedReader(
					new java.io.InputStreamReader(
						new java.io.FileInputStream( filePath), "UTF-8"));
			String	line;
			int		lineNum = 0;
			while(( line = br.readLine()) != null) {
				lineNum++;
				if( lineNum == 1 && line.startsWith( "﻿")) line = line.substring( 1);
				if( lineNum == 1 || line.trim().isEmpty() || line.startsWith( "#")) continue;
				String[]	cols = line.split( ",");
				if( cols.length < 4) {
					results.add( new String[]{ "NG", String.valueOf( lineNum), "-", "-", "-", "-", "列数不足"});
					continue;
				}
				String	fac  = cols[0].trim();
				String	day  = cols[1].trim().replace( '/', '-');
				if( day.matches( "\\d{8}")) {
					day = day.substring( 0,4) + "-" + day.substring( 4,6) + "-" + day.substring( 6,8);
				}
				String	st   = cols[2].trim();
				String	et   = cols[3].trim();
				String[]	dp = day.split( "-");
				String[]	sp = st.split( ":");
				String[]	ep = et.split( ":");
				if( dp.length < 3 || sp.length < 2 || ep.length < 2) {
					results.add( new String[]{ "NG", String.valueOf( lineNum), fac, day, st, et, "形式エラー"});
					continue;
				}
				String	res = reserveDirect( fac, dp[0], dp[1], dp[2], sp[0], sp[1], ep[0], ep[1]);
				String	status = res.contains( "予約しました") ? "OK" : "NG";
				String	reason = res.contains( "予約しました") ? "予約成功" : res;
				results.add( new String[]{ status, String.valueOf( lineNum), fac, day, st, et, reason});
			}
			br.close();
		} catch( Exception e) {
			results.add( new String[]{ "NG", "-", "-", "-", "-", "-", "ファイル読み込みエラー: " + e.getMessage()});
		}
		return	results;
	}

	//// パスワードを変更するメソッド
	public	String	changePassword( String currentPw, String newPw) {
		if( !flagLogin) return "ログインして下さい。";
		String	res = "";
		connectDB();
		try {
			String	sql = "SELECT password FROM db_reservation.user WHERE user_id = ?";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, reservationUserID);
			ResultSet	rs = ps.executeQuery();
			if( rs.next()) {
				if( !rs.getString( "password").equals( currentPw)) {
					ps.close();
					closeDB();
					return "現在のパスワードが違います。";
				}
				ps.close();
				sql = "UPDATE db_reservation.user SET password = ? WHERE user_id = ?";
				ps = sqlCon.prepareStatement( sql);
				ps.setString( 1, newPw);
				ps.setString( 2, reservationUserID);
				ps.executeUpdate();
				ps.close();
			} else {
				ps.close();
				res = "ユーザーが見つかりません。";
			}
		} catch( Exception e) {
			res = "予期しないエラーが発生しました。";
			e.printStackTrace();
		}
		closeDB();
		return	res;
	}

	//// 教室の本日の予約件数を返す
	public	int	getReservationCountToday( String facilityId) {
		int count = 0;
		connectDB();
		try {
			String today = new SimpleDateFormat( "yyyy-MM-dd").format( Calendar.getInstance().getTime());
			String sql = "SELECT COUNT(*) AS c FROM db_reservation.reservation"
					+ " WHERE facility_id = ? AND day = ?";
			PreparedStatement ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, facilityId);
			ps.setString( 2, today);
			ResultSet rs = ps.executeQuery();
			if( rs.next()) count = rs.getInt( "c");
			ps.close();
		} catch( Exception e) { e.printStackTrace(); }
		closeDB();
		return count;
	}

	//// 教室の今後7日間の予約件数を返す
	public	int	getReservationCount7Days( String facilityId) {
		int count = 0;
		connectDB();
		try {
			Calendar cal = Calendar.getInstance();
			String today = new SimpleDateFormat( "yyyy-MM-dd").format( cal.getTime());
			cal.add( Calendar.DAY_OF_MONTH, 7);
			String weekLater = new SimpleDateFormat( "yyyy-MM-dd").format( cal.getTime());
			String sql = "SELECT COUNT(*) AS c FROM db_reservation.reservation"
					+ " WHERE facility_id = ? AND day BETWEEN ? AND ?";
			PreparedStatement ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, facilityId);
			ps.setString( 2, today);
			ps.setString( 3, weekLater);
			ResultSet rs = ps.executeQuery();
			if( rs.next()) count = rs.getInt( "c");
			ps.close();
		} catch( Exception e) { e.printStackTrace(); }
		closeDB();
		return count;
	}

	//// 教室の本日 今からの次の空き時間帯を返す
	public	String	getNextAvailableSlot( String facilityId) {
		String result = "―";
		connectDB();
		try {
			Calendar cal = Calendar.getInstance();
			String today = new SimpleDateFormat( "yyyy-MM-dd").format( cal.getTime());
			String now = new SimpleDateFormat( "HH:mm:ss").format( cal.getTime());

			String sqlF = "SELECT open_time, close_time FROM db_reservation.facility"
					+ " WHERE facility_id = ?";
			PreparedStatement psF = sqlCon.prepareStatement( sqlF);
			psF.setString( 1, facilityId);
			ResultSet rsF = psF.executeQuery();
			String openT = "00:00:00", closeT = "00:00:00";
			if( rsF.next()) {
				openT = rsF.getString( "open_time");
				closeT = rsF.getString( "close_time");
			}
			psF.close();

			if( now.compareTo( closeT) >= 0) { closeDB(); return "本日終了"; }

			String searchStart = ( now.compareTo( openT) > 0) ? now : openT;

			String sqlR = "SELECT start_time, end_time FROM db_reservation.reservation"
					+ " WHERE facility_id = ? AND day = ? AND end_time > ?"
					+ " ORDER BY start_time";
			PreparedStatement psR = sqlCon.prepareStatement( sqlR);
			psR.setString( 1, facilityId);
			psR.setString( 2, today);
			psR.setString( 3, searchStart);
			ResultSet rsR = psR.executeQuery();

			String cursor = searchStart;
			boolean foundGap = false;
			while( rsR.next()) {
				String st = rsR.getString( "start_time");
				String et = rsR.getString( "end_time");
				if( cursor.compareTo( st) < 0) {
					result = cursor.substring( 0, 5) + "～" + st.substring( 0, 5);
					foundGap = true;
					break;
				}
				if( cursor.compareTo( et) < 0) cursor = et;
			}
			psR.close();
			if( !foundGap) {
				if( cursor.compareTo( closeT) < 0) {
					result = cursor.substring( 0, 5) + "～" + closeT.substring( 0, 5);
				} else {
					result = "本日終了";
				}
			}
		} catch( Exception e) { e.printStackTrace(); }
		closeDB();
		return result;
	}

	//// 指定日の全予約情報をリストで返す（グリッド表示用）
	public	ArrayList<String[]>	getReservationsForDate( String date) {
		ArrayList<String[]>	list = new ArrayList<>();
		connectDB();
		try {
			String	sql = "SELECT facility_id, user_id, start_time, end_time FROM db_reservation.reservation"
					+ " WHERE day = ? ORDER BY facility_id, start_time";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, date);
			ResultSet	rs = ps.executeQuery();
			while( rs.next()) {
				list.add( new String[]{
					rs.getString( "facility_id"),
					rs.getString( "user_id"),
					rs.getString( "start_time"),
					rs.getString( "end_time")
				});
			}
			ps.close();
		} catch( Exception e) {
			e.printStackTrace();
		}
		closeDB();
		return	list;
	}

	//// 予約状況確認（当日の全予約を文字列で返す。後方互換用）
	public	String	getReservationStatus() {
		String	res = "";
		connectDB();
		try {
			String	today = new SimpleDateFormat( "yyyy-MM-dd").format( Calendar.getInstance().getTime());
			String	sql = "SELECT facility_id, user_id, start_time, end_time FROM db_reservation.reservation"
					+ " WHERE day = ? ORDER BY facility_id, start_time";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, today);
			ResultSet	rs = ps.executeQuery();
			res = "=== 予約状況確認（" + today + "） ===\n\n";
			boolean	hasData = false;
			while( rs.next()) {
				res += rs.getString( "facility_id") + "教室　";
				res += rs.getString( "start_time").substring( 0, 5) + "～" + rs.getString( "end_time").substring( 0, 5);
				res += "　予約者：" + rs.getString( "user_id") + "\n";
				hasData = true;
			}
			ps.close();
			if( !hasData) res += "本日の予約はありません。";
		} catch( Exception e) {
			res = "予期しないエラーが発生しました。";
			e.printStackTrace();
		}
		closeDB();
		return	res;
	}

	//// 自己予約一覧をリストで返す（画面表示用）
	public	ArrayList<String[]>	getMyReservationsList() {
		ArrayList<String[]>	list = new ArrayList<>();
		if( !flagLogin) return list;
		connectDB();
		try {
			String	today = new SimpleDateFormat( "yyyy-MM-dd").format( new java.util.Date());
			String	sql = "SELECT reservation_id, facility_id, day, start_time, end_time, date FROM db_reservation.reservation"
					+ " WHERE user_id = ? AND day >= ? ORDER BY day, start_time";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, reservationUserID);
			ps.setString( 2, today);
			ResultSet	rs = ps.executeQuery();
			while( rs.next()) {
				list.add( new String[]{
					rs.getString( "facility_id"),
					rs.getString( "day"),
					rs.getString( "start_time"),
					rs.getString( "end_time"),
					rs.getString( "date"),
					String.valueOf( rs.getInt( "reservation_id"))
				});
			}
			ps.close();
		} catch( Exception e) {
			e.printStackTrace();
		}
		closeDB();
		return	list;
	}

	//// 自己予約確認（文字列で返す。後方互換用）
	public	String	getMyReservations() {
		if( !flagLogin) return "ログインして下さい。";
		String	res = "";
		connectDB();
		try {
			String	today = new SimpleDateFormat( "yyyy-MM-dd").format( Calendar.getInstance().getTime());
			String	sql = "SELECT facility_id, day, start_time, end_time FROM db_reservation.reservation"
					+ " WHERE user_id = ? AND day >= ? ORDER BY day, start_time";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, reservationUserID);
			ps.setString( 2, today);
			ResultSet	rs = ps.executeQuery();
			res = "=== 自己予約確認（" + reservationUserID + "） ===\n\n";
			boolean	hasData = false;
			while( rs.next()) {
				res += rs.getString( "day") + "　";
				res += rs.getString( "facility_id") + "教室　";
				res += rs.getString( "start_time").substring( 0, 5) + "～" + rs.getString( "end_time").substring( 0, 5) + "\n";
				hasData = true;
			}
			ps.close();
			if( !hasData) res += "本日以降の予約はありません。";
		} catch( Exception e) {
			res = "予期しないエラーが発生しました。";
			e.printStackTrace();
		}
		closeDB();
		return	res;
	}

	//// 新規予約ボタン押下時（ReservationDialogベース・後方互換用）
	public	String	makeReservation( MainFrame frame) {
		String	res = "";
		if( !flagLogin) return "ログインして下さい。";

		ReservationDialog	rd = new ReservationDialog( frame, this);
		rd.setVisible( true);
		if( rd.canceled) return res;

		String	ryear  = rd.tfYear.getText();
		String	rmonth = rd.tfMonth.getText();
		String	rday   = rd.tfDay.getText();
		String	startH = rd.startHour.getSelectedItem();
		String	startM = rd.startMinute.getSelectedItem();
		String	endH   = rd.endHour.getSelectedItem();
		String	endM   = rd.endMinute.getSelectedItem();
		String	facility = rd.choiceFacility.getSelectedItem();

		return reserveDirect( facility, ryear, rmonth, rday, startH, startM, endH, endM);
	}

	//// 指定教室の利用可能開始・終了時間を取得する
	//// （戻り値：availableTime[][]={{ 開始時, 開始分}, { 終了時, 終了分}}
	public	int[][] getAvailableTime( String facility) {
		int [][] availableTime = {{ 0, 0}, { 0, 0}};
		connectDB();
		try {
			String		sql = "SELECT open_time, close_time FROM db_reservation.facility WHERE facility_id = ?";
			PreparedStatement	ps = sqlCon.prepareStatement( sql);
			ps.setString( 1, facility);
			ResultSet	rs = ps.executeQuery();
			if( rs.next()) {
				String	timeData = rs.getString( "open_time");
				availableTime[0][0] = Integer.parseInt( timeData.substring( 0, 2));
				availableTime[0][1] = Integer.parseInt( timeData.substring( 3, 5));
				timeData = rs.getString( "close_time");
				availableTime[1][0] = Integer.parseInt( timeData.substring( 0, 2));
				availableTime[1][1] = Integer.parseInt( timeData.substring( 3, 5));
			}
			ps.close();
		} catch( Exception e) {
			e.printStackTrace();
		}
		closeDB();		// BUG-ST-003 修正
		return	availableTime;
	}
}
