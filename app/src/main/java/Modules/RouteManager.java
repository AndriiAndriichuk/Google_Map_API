package Modules;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


//TODO Клас стверний для створення маршруту між двома точками
public class RouteManager implements DirectionFinderListener{
    Activity myActivity;
    private ProgressDialog progressDialog;
    private List<Marker> originMarkers;
    private List<Marker> destinationMarkers;
    private List<Polyline> polylinePaths;
    private GoogleMap mMap;
    private int width;
    private Color color;

    public RouteManager(Activity activity, ProgressDialog progressDialog, GoogleMap mMap) {
        this.myActivity = activity;
        this.progressDialog = progressDialog;
        this.originMarkers = new ArrayList<>();
        this.destinationMarkers = new ArrayList<>();
        this.polylinePaths = new ArrayList<>();
        this.mMap = mMap;
        this.width = 10;
        this.color = new Color();
        this.color.red(51);
        this.color.blue(204);
        this.color.green(204);
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(myActivity, "Please wait.",
                "Finding direction..!", true);
        if (originMarkers != null)
            for (Marker marker : originMarkers)
                marker.remove();
        if (destinationMarkers != null)
            for (Marker marker : destinationMarkers)
                marker.remove();
        if (polylinePaths != null)
            for (Polyline polyline:polylinePaths )
                polyline.remove();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route1 : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route1.startLocation, 16));
            Toast.makeText(myActivity, "Тривалість :" + route1.duration.text + "\nВідстань:" + route1.distance.text, Toast.LENGTH_LONG).show();
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route1.startAddress)
                    .position(route1.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route1.endAddress)
                    .position(route1.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(51,204,204)).
                    width(this.width);

            for (int i = 0; i < route1.points.size(); i++)
                polylineOptions.add(route1.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    public void sendRequest() {
        LatLng Maruska = new LatLng(48.394710, 25.952522);
        LatLng MyLocation = new LatLng(48.372625,25.930097);
        String origin = Maruska.latitude + "," + Maruska.longitude;
        String destination = MyLocation.latitude + "," + MyLocation.longitude;
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(LatLng origin,LatLng destination) {
        String originS = origin.latitude + "," + origin.longitude;
        String destinationS = destination.latitude + "," + destination.longitude;
        try {
            new DirectionFinder(this, originS, destinationS).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
