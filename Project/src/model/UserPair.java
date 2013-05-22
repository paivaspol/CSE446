package model;

public class UserPair{
	private String user1;
	private String user2;
	
	public UserPair(String u1, String u2){
		if(u1.compareTo(u2) < 0){
			this.user1 = u1;
			this.user2 = u2;
		}else{
			this.user1 = u2;
			this.user2 = u1;
		}
	}
	
	@Override
	public boolean equals(Object other){
		if(!(other instanceof UserPair)){
			return false;
		}else{
			UserPair o = (UserPair) other;
			return o.user1.equals(this.user1) &&
					o.user2.equals(this.user2);
		}
	}
	
	@Override
	public int hashCode(){
		return this.user1.hashCode() + 31 * this.user2.hashCode();
	}
}