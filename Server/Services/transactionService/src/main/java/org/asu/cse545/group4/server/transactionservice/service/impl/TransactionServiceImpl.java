package org.asu.cse545.group4.server.transactionservice.service.impl;

import org.asu.cse545.group4.server.transactionservice.dao.TransactionDAO;
import org.asu.cse545.group4.server.transactionservice.service.TransactionService;
import org.asu.cse545.group4.server.sharedobjects.TblTransaction;
import org.asu.cse545.group4.server.sharedobjects.TblAccount;
import org.asu.cse545.group4.server.sharedobjects.TblUserProfile;
import org.asu.cse545.group4.server.sharedobjects.TblUser;
import org.asu.cse545.group4.server.sharedobjects.TblRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.asu.cse545.group4.server.requestservice.service.RequestService;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {
	@Autowired
	private TransactionDAO transactionDAO;
	@Autowired
	private RequestService reqService;


	@Transactional
	public String addTransaction(TblTransaction transaction, int userId, int type) {
		String status = this.transactionDAO.addTransaction(transaction,userId);
		String[] responses = status.split(":");
		if (responses[0].equals("OK")) {
		 	String transId = responses[1];
		 	TblTransaction thisTrans = new TblTransaction(Integer.parseInt(transId));
		 	TblUser user = new TblUser(userId);
		 	TblRequest newReq = new TblRequest();
		 	newReq.setTblUserByRequestedBy(user);
		 	newReq.setTblTransaction(thisTrans);
		 	newReq.setTypeOfRequest(type);
		 	reqService.addRequest(newReq);
		 }		 
		 return responses[0];		 
	}

	@Transactional
	public String approveTransaction(int transactionId, int approverId)
	{
		TblUser approver = new TblUser(approverId);
		if (reqService.isTierTwoEmployee(approver) || reqService.isAdmin(approver) || reqService.isTierOneEmployee(approver)) 
		{
			TblTransaction transaction = new TblTransaction(transactionId);
			TblRequest request = reqService.getRequest(transaction);
			request.setTblUserByRequestAssignedTo(approver);
			reqService.assignTo(request);
			return this.transactionDAO.approveTransaction(transactionId,approverId);
		}
		else
		{
			return "INSUFFICIENT_PRIVILIGES";
		}
		
	}

	@Transactional
	public String declineTransaction(int transactionId, int declinerId)
	{
		TblUser decliner = new TblUser(declinerId);
		if (reqService.isTierTwoEmployee(decliner) || reqService.isAdmin(decliner) || reqService.isTierOneEmployee(decliner)) 
		{
			TblTransaction transaction = new TblTransaction(transactionId);
			TblRequest request = reqService.getRequest(transaction);
			request.setTblUserByRequestAssignedTo(decliner);
			reqService.assignTo(request);
			return this.transactionDAO.declineTransaction(transactionId,declinerId);
		}
		else
		{
			return "INSUFFICIENT_PRIVILIGES";
		}
	}

	@Transactional
	public  String searchAccount(TblUserProfile userProfile)
	{
		return this.transactionDAO.searchAccount(userProfile);
	}

	@Transactional
	public TblTransaction getTransaction(TblTransaction transaction)
	{
		return this.transactionDAO.getTransaction(transaction);
	}

	@Transactional
	public  List<TblTransaction> getTransactionsForAccount(TblAccount account)
	{
		return this.transactionDAO.getTransactionsForAccount(account);
	}

	@Transactional
	public  List<TblAccount> getAccountsForUser(TblUser user)
	{
		return this.transactionDAO.getAccountsForUser(user);
	}


	@Transactional
	public  TblAccount updateAccount(TblAccount account, TblUser user)
	{
		if ( isThisUserAccount(account,user) || reqService.isTierTwoEmployee(user) || reqService.isAdmin(user))
		{
			return this.transactionDAO.updateAccount(account,user);
		}
		return null;		
	}

	@Transactional
	public String deleteAccount(TblAccount account, TblUser user)
	{
		if ( reqService.isTierTwoEmployee(user) || reqService.isAdmin(user))
		{
			this.transactionDAO.deleteAccount(account,user);
			return "SUCCESS";
		}
		else
		{
			return "INSUFFICIENT_PRIVILIGES";
		}
	}

	
	@Transactional
	public  List<TblAccount> searchAccountByAccountParams(TblAccount account)
	{
		return this.transactionDAO.searchAccountByAccountParams(account);
	}

	@Transactional
	public  boolean isThisUserAccount(TblAccount account, TblUser user)
	{
		return this.transactionDAO.isThisUserAccount(account , user);
	}

	@Transactional
	public TblAccount createAccount(TblRequest  request  , TblUser approver)
	{
		if(reqService.isTierTwoEmployee(approver) || reqService.isAdmin(approver))
		{
			return this.transactionDAO.createAccount(request, approver);
		}
		else
		{
			return null;
		}
	}

	@Transactional
	public  TblUser getUser(TblUser user)
	{
		return this.transactionDAO.getUser(user);
	}
	
	@Transactional
	public  String getAllAccounts()
	{
		return this.transactionDAO.getAllAccounts();
	}
	
}
