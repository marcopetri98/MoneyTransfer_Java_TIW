package it.polimi.tiw.pure_html.dao;

import it.polimi.tiw.pure_html.beans.Account;
import it.polimi.tiw.pure_html.beans.Transfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferDAO extends GeneralDAO {
	public TransferDAO(Connection conn) {
		super(conn);
	}
	public TransferDAO(Connection conn, String language, String country) {
		super(conn,language,country);
	}

	/* **************************
	 *							*
	 * 		MAIN METHODS		*
	 * 							*
	 ****************************/
	public Transfer findTransfer(int transferId) throws IllegalArgumentException, SQLException {
		/* used variables */
		String query;
		PreparedStatement preparedStatement;
		ResultSet result;
		Account ingoingAccount, outgoingAccount;
		AccountDAO accountDAO = new AccountDAO(conn);

		/* exception handling */
		if (transferId < 0) {
			throw new IllegalArgumentException(selectedLanguage.getString("dbTransferInvalidCode"));
		}

		/* main code */
		query = "SELECT * FROM Transfer WHERE Id=?";
		preparedStatement = conn.prepareStatement(query);
		preparedStatement.setInt(1,transferId);
		result = preparedStatement.executeQuery();
		if (result.first()) {
			ingoingAccount = accountDAO.findAccount(result.getInt("IngoingAccount"));
			outgoingAccount = accountDAO.findAccount(result.getInt("OutgoingAccount"));
			return new Transfer(result.getInt("Id"),outgoingAccount,ingoingAccount,result.getTimestamp("Date"),result.getInt("Amount"),result.getString("Reason"));
		} else {
			return null;
		}
	}

	public void createTransfer(int outgoing, int ingoing, String reason, int amount) throws IllegalArgumentException, SQLException {
		/* used variables */
		String query;
		PreparedStatement preparedStatement;
		ResultSet result;
		Account ingoingAccount, outgoingAccount;
		AccountDAO accountDAO = new AccountDAO(conn);

		/* exception handling */
		if (outgoing < 0 || ingoing < 0 || reason.length() > 200 || amount <= 0) {
			throw new IllegalArgumentException(selectedLanguage.getString("dbTransferWrongParams"));
		} else {
			outgoingAccount = accountDAO.findAccount(outgoing);
			ingoingAccount = accountDAO.findAccount(ingoing);
			if (outgoingAccount == null || ingoingAccount == null) {
				throw new IllegalArgumentException(selectedLanguage.getString("dbTransferIngoingOutgoingError"));
			}
		}

		/* main code */
		query = "INSERT INTO Transfer (OutgoingAcocunt,IngoingAccount,Date,Amount,Reason) VALUES (?,?,now(),?,?)";
		preparedStatement = conn.prepareStatement(query);
		preparedStatement.setInt(1,outgoing);
		preparedStatement.setInt(2,ingoing);
		preparedStatement.setInt(3,amount);
		preparedStatement.setString(4,reason);
		preparedStatement.executeUpdate();
	}
}
