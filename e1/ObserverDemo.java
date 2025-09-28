import java.util.*;

interface Observer {
    void update(float temperature);
}

class PhoneDisplay implements Observer {
    public void update(float temperature) {
        System.out.println("Phone Display updated: Temp = " + temperature + "°C");
    }
}

class TVDisplay implements Observer {
    public void update(float temperature) {
        System.out.println("TV Display updated: Temp = " + temperature + "°C");
    }
}

class WeatherStation {
    private List<Observer> observers = new ArrayList<>();
    private float temperature;

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
        notifyObservers();
    }

    private void notifyObservers() {
        for (Observer o : observers) {
            o.update(temperature);
        }
    }
}

public class ObserverDemo {
    public static void main(String[] args) {
        WeatherStation ws = new WeatherStation();
        ws.addObserver(new PhoneDisplay());
        ws.addObserver(new TVDisplay());
        ws.setTemperature(28.5f);
    }
}
