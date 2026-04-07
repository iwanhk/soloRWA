export class InfoMask{
    static phone(phone:string){
        return phone.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2');
    }
    
    static email(email:string){
        return email.replace(/(.{2}).*(.{2})@(.*)/, '$1****$2@$3');
    }
}