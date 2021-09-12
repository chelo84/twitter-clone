# twitter-clone

A twitter clone API and CLI to access the API.

## API

twitter clone's API developed using reactive technology with Spring Webflux and [RSocket](https://rsocket.io).

### Rest

#### /signup
Create a new user.

#### /signin
Login, this endpoint returns a Bearer token in the response's `Authorization` header.

### Reactive streams with RSocket

#### Follow

[METADATA_PUSH](https://rsocket.io/about/protocol/#frame-metadata-push) _["follow"]_
> Description: Connects to receive notifications from the server when a user is followed/unfollowed.

[REQUEST_RESPONSE](https://rsocket.io/about/protocol/#frame-request-response) _["follow"]_
> Description: Follow a user.
> 
> Arguments: 
>
> `username` → username of the user to be followed.

[REQUEST_RESPONSE](https://rsocket.io/about/protocol/#frame-request-response) _["unfollow"]_
> Description: Unfollow a user.
> 
> Arguments: 
>
> `username` → username of the user to be unfollowed.

#### Tweet

[METADATA_PUSH](https://rsocket.io/about/protocol/#frame-metadata-push) _["tweets.{*username*}"]_
> Description: Connects to receive notifications from the server when _username_ tweets.

[REQUEST_RESPONSE](https://rsocket.io/about/protocol/#frame-request-response) _["tweet"]_
> Description: Post a new tweet.
> 
> Arguments:
>
> `text` → tweet's text;
>
> `replyTo` _(optional)_ → Sent when the new tweet is a reply to another tweet.

[REQUEST_STREAM](https://rsocket.io/about/protocol/#frame-request-stream) _["tweets"]_
> Description: Fetch a user's tweets
>
> Arguments:
>
> `username` → User's username
>
> `page` → Page to look for
>
> `size` → Size of each page
>
> _(Check if it isn't better to use a REQUEST_STREAM instead...)_

#### Profile

[REQUEST_RESPONSE](https://rsocket.io/about/protocol/#frame-request-response) _["profile.user"]_
> Description: Fetch the signed-in user's profile

### CLI

CLI developed using Spring Shell, available commands are:

```
Follow Command
        follow: Follow a user
        unfollow: Unfollow a user

Profile Command
        show-profile: Display list of users

Sign Command
        sign-in: Sign in as user
        sign-up: Sign up user

Tweet Command
        tweet: Post a new tweet
        tweets: Get tweets from a user
```
