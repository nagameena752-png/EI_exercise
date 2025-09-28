interface TypeCCharger {
    void chargeWithTypeC();
}

class OldMicroUSBPhone {
    public void chargeWithMicroUSB() {
        System.out.println("Charging phone with MicroUSB port.");
    }
}

class MicroUSBToTypeCAdapter implements TypeCCharger {
    private OldMicroUSBPhone oldPhone;

    public MicroUSBToTypeCAdapter(OldMicroUSBPhone oldPhone) {
        this.oldPhone = oldPhone;
    }

    public void chargeWithTypeC() {
        oldPhone.chargeWithMicroUSB();
    }
}

public class AdapterDemo {
    public static void main(String[] args) {
        OldMicroUSBPhone oldPhone = new OldMicroUSBPhone();
        TypeCCharger adapter = new MicroUSBToTypeCAdapter(oldPhone);
        adapter.chargeWithTypeC();
    }
}
