package domain;

import java.util.ArrayList;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;

import dataAccess.CustomerDAO;
import dataAccess.MerchantDAO;
import dataAccess.TransactionDAO;

public class Transaction {
	String transactionRefNo;
	String customerID;
	String currencyCode; 
	OTP OTP;
	ArrayList<PaymentObject> payment;
	String merchantID;
	String transactionType;
	String status;
	float requestedAmount;
	Transaction authTransaction;

	
	public Transaction() {
		super();
		this.transactionRefNo = Generators.randomBasedGenerator().generate().toString();
	}
	public Transaction getAuthTransaction() {
		return authTransaction;
	}
	public void setAuthTransaction(Transaction authTransaction) {
		try{
			new TransactionDAO().deleteObjectWithKey("transactionRefNo",authTransaction.getTransactionRefNo());
			//TODO:Remove this jugaad.
		}
		catch(Exception e)
		{
			
		}
		this.authTransaction = authTransaction;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	
	@JsonIgnore
	public float getConsolidatedPaymentAmount(){
		String destinationCurrencyCode = "INR";
		//TODO: The above parameter should be input.
		float total = 0; 
		for(PaymentObject p:this.getPayment()){
			total += p.convertToOtherCurrency(destinationCurrencyCode);
		}
		return total;
	}
	
	public float getRequestedAmount() {
		return requestedAmount;
	}
	public void setRequestedAmount(float amount) {
		this.requestedAmount = amount;
	}
	public ArrayList<PaymentObject> getPayment() {
		return payment;
	}
	public void setPayment(ArrayList<PaymentObject> payment) {
		this.payment = payment;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTransactionRefNo() {
		return transactionRefNo;
	}
	public void setTransactionRefNo(String transactionRefNo) {
		this.transactionRefNo = transactionRefNo;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getMerchantID() {
		return merchantID;
	}
	public void setMerchantID(String merchantID) {
		this.merchantID = merchantID;
	}
	public OTP getOTP() {
		return OTP;
	}
	public void setOTP(OTP oTP) {
		OTP = oTP;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((OTP == null) ? 0 : OTP.hashCode());
		result = prime * result + ((authTransaction == null) ? 0 : authTransaction.hashCode());
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((customerID == null) ? 0 : customerID.hashCode());
		result = prime * result + ((merchantID == null) ? 0 : merchantID.hashCode());
		result = prime * result + ((payment == null) ? 0 : payment.hashCode());
		result = prime * result + Float.floatToIntBits(requestedAmount);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((transactionRefNo == null) ? 0 : transactionRefNo.hashCode());
		result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Transaction)) {
			return false;
		}
		Transaction other = (Transaction) obj;
		if (OTP == null) {
			if (other.OTP != null) {
				return false;
			}
		} else if (!OTP.equals(other.OTP)) {
			return false;
		}
		if (authTransaction == null) {
			if (other.authTransaction != null) {
				return false;
			}
		} else if (!authTransaction.equals(other.authTransaction)) {
			return false;
		}
		if (currencyCode == null) {
			if (other.currencyCode != null) {
				return false;
			}
		} else if (!currencyCode.equals(other.currencyCode)) {
			return false;
		}
		if (customerID == null) {
			if (other.customerID != null) {
				return false;
			}
		} else if (!customerID.equals(other.customerID)) {
			return false;
		}
		if (merchantID == null) {
			if (other.merchantID != null) {
				return false;
			}
		} else if (!merchantID.equals(other.merchantID)) {
			return false;
		}
		if (payment == null) {
			if (other.payment != null) {
				return false;
			}
		} else if (!payment.equals(other.payment)) {
			return false;
		}
		if (Float.floatToIntBits(requestedAmount) != Float.floatToIntBits(other.requestedAmount)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		if (transactionRefNo == null) {
			if (other.transactionRefNo != null) {
				return false;
			}
		} else if (!transactionRefNo.equals(other.transactionRefNo)) {
			return false;
		}
		if (transactionType == null) {
			if (other.transactionType != null) {
				return false;
			}
		} else if (!transactionType.equals(other.transactionType)) {
			return false;
		}
		return true;
	}
	public void openTransaction(Transaction partialTransactionFromTheCustomer) throws Exception {
		//TODO : Check if the OTP is available.
		new TransactionDAO().createNewCashTransaction(partialTransactionFromTheCustomer);
		//TODO: Open a new transaction here. i.e insert the details of the new transaction into the DB. 
		
	}
	public Transaction processTransaction(Transaction partialTransactionFromTheMerchant) throws Exception {
		CustomerDAO customerDAO = new CustomerDAO();
		MerchantDAO merchantDAO = new MerchantDAO();
		TransactionDAO transactionDAO = new TransactionDAO();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Document mongoDocument = transactionDAO.getObjectByKeyValuePair("otp.OTP", partialTransactionFromTheMerchant.getOTP().getOTP());
		Transaction transaction = mapper.readValue(mongoDocument.toJson(),Transaction.class);
		mongoDocument = customerDAO.getObjectByKeyValuePair("customerID", transaction.getCustomerID());
		Customer customer = mapper.readValue(mongoDocument.toJson(), Customer.class);
		transaction = customer.settleTransaction(partialTransactionFromTheMerchant);
		//TODO:If the process fails here, roll back the transaction to give the customer his money back.
		mongoDocument = merchantDAO.getObjectByKeyValuePair("merchantID", merchantID);
		Merchant merchant = mapper.readValue(mongoDocument.toJson(), Merchant.class);
		merchant.collectPayment(transaction);
		merchantDAO.updateObjectWithKey("merchantID", merchantID, merchant);
		customerDAO.updateObjectWithKey("customerID", customerID, customer);
		transactionDAO.updateObjectWithKey("transactionRefNo", transactionRefNo, transaction);
		return transaction;
	}
	
}
