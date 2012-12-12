package spring.security

class Role {

	String authority
        
    static mapping = {
        table "shiro_role"
        authority column: "name"
    }

	static constraints = {
		authority blank: false, unique: true
	}
}