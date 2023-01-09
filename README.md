# Tajo

> "tajo" is a spanish word meaning a deep cut

## Introduction

Hello! At MovingWorlds we're looking forward to launching an URL-shortening API service, so that users may have custom URLs for their long URLs.

## User stories:

 - As a user, I want to share a long URL as a short URL so it's easier to copy/paste into emails.
 - As a user, sometimes I will want to customize the short URL so that I can recall what URL it is referencing or give it a cool name
 - As a user, I will want to see a report of my short URLs, when I created them, and how many times it was clicked.

## Here are its specifications:

 - A user can submit a URL to /submit endpoint without a shortcode proposed, and receive automatically allocated unique shortcode in response.
 - A user can submit a URL to /submit with the desired shortcode and will receive the chosen shortcode if it is available.
 - A user can access a /<shortcode> endpoint and be redirected to the URL associated with that shortcode if it exists.
 - All shortcodes can contain digits, upper case letters, and lowercase letters. It is case in-sensitive.
 - “Automatically” allocated shortcodes are exactly 6 characters long.
 - User-submitted shortcodes must be at least 4 characters long.
 - A user can access a /<shortcode>/stats endpoint in order to see when the shortcode was registered, when it was last accessed, and how many times it was accessed.

## Guidelines

 - It’s not about the final code. You are free to use whatever works for you and explains the proposed solution best
 - You are free to use whichever technology/language you feel most comfortable with, as long as you feel comfortable with
 - When thinking of solutions - prioritize the end-user experience and fulfilling all user stories, not the admin or cost of infrastructure costs
 - Think of the best reliability of the system
 - Think of data design patterns that are scalable
 - Think of a testable approach

### Here's what we may or may not want to see:

 - You may use a database (relational or not), or store everything in memory.
 - You may develop a frontend for the service, or keep it as merely an API.
 - You don’t have to focus on creating instructions to run/deploy the code/application by us, focus on the system design you are building and fulfill customer user stories. We want to see your thinking process, not the final code.
