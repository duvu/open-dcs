package com.vd5.dcs.geocoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * @author beou on 9/12/18 21:51
 */
public class HereGeocoder extends JsonGeocoder {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    public HereGeocoder(String url, String app_code, AddressFormat addressFormat) {
        super(url + "&app_code=" + app_code + "&prox=%f,%f&maxresults=1&gen=9", addressFormat);
    }

//    {
//        "Response": {
//        "MetaInfo": {
//            "Timestamp": "2018-09-12T14:12:12.551+0000",
//                    "NextPageInformation": "2"
//        },
//        "View": [
//        {
//            "_type": "SearchResultsViewType",
//                "ViewId": 0,
//                "Result": [
//            {
//                "Relevance": 1,
//                    "Distance": 13.6,
//                    "MatchLevel": "houseNumber",
//                    "MatchQuality": {
//                "Country": 1,
//                        "State": 1,
//                        "County": 1,
//                        "City": 1,
//                        "District": 1,
//                        "Street": [
//                1
//              ],
//                "HouseNumber": 1,
//                        "PostalCode": 1
//            },
//                "MatchType": "pointAddress",
//                    "Location": {
//                "LocationId": "NT_Opil2LPZVRLZjlWNLJQuWB_0ITN",
//                        "LocationType": "address",
//                        "DisplayPosition": {
//                    "Latitude": 41.88432,
//                            "Longitude": -87.63877
//                },
//                "NavigationPosition": [
//                {
//                    "Latitude": 41.88449,
//                        "Longitude": -87.63877
//                }
//              ],
//                "MapView": {
//                    "TopLeft": {
//                        "Latitude": 41.8854442,
//                                "Longitude": -87.64028
//                    },
//                    "BottomRight": {
//                        "Latitude": 41.8831958,
//                                "Longitude": -87.63726
//                    }
//                },
//                "Address": {
//                    "Label": "425 W Randolph St, Chicago, IL 60606, United States",
//                            "Country": "USA",
//                            "State": "IL",
//                            "County": "Cook",
//                            "City": "Chicago",
//                            "District": "West Loop",
//                            "Street": "W Randolph St",
//                            "HouseNumber": "425",
//                            "PostalCode": "60606",
//                            "AdditionalData": [
//                    {
//                        "value": "United States",
//                            "key": "CountryName"
//                    },
//                    {
//                        "value": "Illinois",
//                            "key": "StateName"
//                    },
//                    {
//                        "value": "Cook",
//                            "key": "CountyName"
//                    },
//                    {
//                        "value": "N",
//                            "key": "PostalCodeType"
//                    }
//                ]
//                },
//                "MapReference": {
//                    "ReferenceId": "776372180",
//                            "MapId": "NAAM18124",
//                            "MapVersion": "Q1/2018",
//                            "MapReleaseDate": "2018-08-28",
//                            "Spot": 0.52,
//                            "SideOfStreet": "right",
//                            "CountryId": "21000001",
//                            "StateId": "21002247",
//                            "CountyId": "21002623",
//                            "CityId": "21002647",
//                            "BuildingId": "9000000000002726912",
//                            "AddressId": "79186508"
//                }
//            }
//            }
//        ]
//        }
//    ]
//    }
//    }
    @Override
    public Address parseAddress(JsonObject json) {
        JsonObject jsonObject = json.getJsonObject("Response").getJsonArray("View").getJsonObject(0);
        JsonObject locationObject = jsonObject.getJsonArray("Result").getJsonObject(0).getJsonObject("Location");
        JsonObject addressObject = locationObject.getJsonObject("Address");

        Address address = new Address();

        address.setCountry(addressObject.getString("Country", ""));
        address.setDistrict(addressObject.getString("District", ""));
        address.setHouse(addressObject.getString("HouseNumber", ""));
        address.setPostcode(addressObject.getString("PostalCode", ""));
        address.setState(addressObject.getString("State", ""));
        address.setStreet(addressObject.getString("Street", ""));

        return address;
    }

    @Override
    public String getName() {
        return "HereGeocoder";
    }
}
