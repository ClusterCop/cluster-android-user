# TRANXIT REVAMP NEW


##Installation Guide

```
git clone [PROJECT]
```

Delete ```.git``` folder in Project folder

Rename Package using refactor


## Getting Started

Build.gradle
```
applicationId "com.tranxit.app"
buildConfigField "String", "BASE_URL", '"http://schedule.deliveryventure.com/"'
buildConfigField "String", "CLIENT_SECRET", '"yVnKClKDHPcDlqqO1V05RtDRdvtrVHfvjlfqliha"'
buildConfigField "String", "CLIENT_ID", '"2"'
buildConfigField "String", "STRIPE_PK", '"pk_test_0G4SKYMm8dK6kgayCPwKWTXy"'
buildConfigField "String", "PAYPAL_CLIENT_TOKEN", '"eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJlMWMzMGE4YmJlZmZlODk4MDJlMmY2ZGM5MzE4NjE1ZmJmZDQ4YWFjMTc3Y2ZkN2YxZjE4MDc1YjMzMzFkYmQ2fGNyZWF0ZWRfYXQ9MjAxOC0wOC0wN1QwNjo0MDoxMS4zNjI0Nzg4MzIrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vb3JpZ2luLWFuYWx5dGljcy1zYW5kLnNhbmRib3guYnJhaW50cmVlLWFwaS5jb20vMzQ4cGs5Y2dmM2JneXcyYiJ9LCJ0aHJlZURTZWN1cmVFbmFibGVkIjp0cnVlLCJwYXlwYWxFbmFibGVkIjp0cnVlLCJwYXlwYWwiOnsiZGlzcGxheU5hbWUiOiJBY21lIFdpZGdldHMsIEx0ZC4gKFNhbmRib3gpIiwiY2xpZW50SWQiOm51bGwsInByaXZhY3lVcmwiOiJodHRwOi8vZXhhbXBsZS5jb20vcHAiLCJ1c2VyQWdyZWVtZW50VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3RvcyIsImJhc2VVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbSIsImFzc2V0c1VybCI6Imh0dHBzOi8vY2hlY2tvdXQucGF5cGFsLmNvbSIsImRpcmVjdEJhc2VVcmwiOm51bGwsImFsbG93SHR0cCI6dHJ1ZSwiZW52aXJvbm1lbnROb05ldHdvcmsiOnRydWUsImVudmlyb25tZW50Ijoib2ZmbGluZSIsInVudmV0dGVkTWVyY2hhbnQiOmZhbHNlLCJicmFpbnRyZWVDbGllbnRJZCI6Im1hc3RlcmNsaWVudDMiLCJiaWxsaW5nQWdyZWVtZW50c0VuYWJsZWQiOnRydWUsIm1lcmNoYW50QWNjb3VudElkIjoiYWNtZXdpZGdldHNsdGRzYW5kYm94IiwiY3VycmVuY3lJc29Db2RlIjoiVVNEIn0sIm1lcmNoYW50SWQiOiIzNDhwazljZ2YzYmd5dzJiIiwidmVubW8iOiJvZmYifQ=="'
```


string.xml
```
<string name="app_name" translatable="false">Tranxit User</string>
<string name="google_map_key" translatable="false">AIzaSyAdXoVw-M-g4vgEOZZK7Dc9jMUlLR5xVXI</string>
<string name="FACEBOOK_APP_ID" translatable="false">227306957774573</string>
<string name="ACCOUNT_KIT_CLIENT_TOKEN" translatable="false">3f73b9bdd9f499561e32e834e7dcd1f8</string>
<string name="google_signin_server_client_id" translatable="false">405772036882-ib15rm0b0f5cq3mpqbljk9b28f6d0j4u.apps.googleusercontent.com</string>
```

[google_signin_server_client_id](https://console.cloud.google.com/) -API&Services -> Credentials -> OAuth 2.0 client IDs -> Web client (auto created by Google Service)



Add app in [firebase](http://console.firebase.google.com/), and don't forgot to add SHA-1

[Firebase console](http://console.firebase.google.com/) -Realtime Database -> set database rules as below
```
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```



