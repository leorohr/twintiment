package org.twintiment.analysis.geolocation;

import org.twintiment.dto.Settings;

/**
 * A utitlity class for dealing with geographical coordinates.
 */
public class GeoUtils {
	
	private static final double R = 6372.8; // In kilometers
	/**
	 * Implementation of the haversine formula for great circle distances. (https://en.wikipedia.org/wiki/Haversine_formula)
	 * @param lat1 Latitude of p1.
	 * @param lon1 Longitude of p1.
	 * @param lat2 Latitude of p2.
	 * @param lon2 Longitude of p2.
	 * @return The distance between two points p1 and p2. 
	 */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
    
    /**
	 * Checks whether the provided coords are included in any of the squares given
	 * in {@link Settings}. Assumption: The point passed as {@code point} is within 
	 * the rectangle, iff the distance from the point to any of the corners of the 
	 * rectangle is greater than the distance between any of the corners of the rectangle.
	 * @param point The coordinates to check. (lat/lon)
	 * @return {@code true} if the point is included in any of the squares,
	 * 		{@code false} otherwise. 
	 */
	public static boolean pointInRect(LatLng point, LatLng[] rect) {
		
		//Points A,B,C,D starting bottom left, clockwise
		double AB = haversine(	rect[0].getLat(), rect[0].getLng(),
										rect[1].getLat(), rect[1].getLng());
		double BC = haversine(	rect[1].getLat(), rect[1].getLng(),
										rect[2].getLat(), rect[2].getLng());
		
		double PA = haversine(	point.getLat(), point.getLng(),
										rect[0].getLat(), rect[0].getLng());
		if(PA > AB && PA > BC)
			return false;
		
		double PB = haversine(	point.getLat(), point.getLng(),
										rect[1].getLat(), rect[1].getLng());
		if(PB > AB && PB > BC)
			return false;
		
		double PC = haversine(	point.getLat(), point.getLng(),
										rect[2].getLat(), rect[2].getLng());
		if(PC > AB && PC > BC)
			return false;
		
		double PD = haversine(	point.getLat(), point.getLng(),
										rect[3].getLat(), rect[3].getLng());
		if(PD > AB && PD > BC)
			return false;
		
		return true;
	}
    
	/**
	 * A helper class to represent a point in the geo-coordinate system using 
	 * latitude and longitude.
	 */
	public static class LatLng {
		
		private double lat;
		private double lng;
		
		public LatLng() {
			
		}
		
		public LatLng(double lat, double lng) {
			this.lat = lat;
			this.lng = lng;
		}
		
		public double getLat() {
			return lat;
		}
		
		public void setLat(double lat) {
			this.lat = lat;
		}
		
		public double getLng() {
			return lng;
		}
		
		public void setLng(double lng) {
			this.lng = lng;
		}
	}
	
	
}
