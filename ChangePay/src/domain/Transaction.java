package domain;

import java.util.ArrayList;

import com.fasterxml.uuid.Generators;

import dataAccess.TransactionDAO;

public class Transaction {
	String transactionRefNo;
	String currencyCode; 
	OTP OTP;
	ArrayList<PaymentObject> payment;
	Merchant merchant;
	String transactionType;
	String status;
	float requestedAmount;

	
	public Transaction() {
		super();
		this.transactionRefNo = Generators.randomBasedGenerator().generate().toString();
	}
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
	public Merchant getMerchant() {
		return merchant;
	}
	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
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
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((merchant == null) ? 0 : merchant.hashCode());
		result = prime * result + ((payment == null) ? 0 : payment.hashCode());
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
		if (currencyCode == null) {
			if (other.currencyCode != null) {
				return false;
			}
		} else if (!currencyCode.equals(other.currencyCode)) {
			return false;
		}
		if (merchant == null) {
			if (other.merchant != null) {
				return false;
			}
		} else if (!merchant.equals(other.merchant)) {
			return false;
		}
		if (payment == null) {
			if (other.payment != null) {
				return false;
			}
		} else if (!payment.equals(other.payment)) {
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
		new TransactionDAO().createNewTransaction(partialTransactionFromTheCustomer);
		//TODO: Open a new transaction here. i.e insert the details of the new transaction into the DB. 
		
	}
	public static void processTransaction(Transaction partialTransactionFromTheMerchant) {
		
		//TODO: Process a request here. 
		
	}
	
}
