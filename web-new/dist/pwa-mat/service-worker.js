"use strict";var precacheConfig=[["app.ac477bee3d0d82403bab89d5a392a57a.css","a668563f8137b9d29ec4e4148cdbc0d3"],["fonts/MaterialIcons-Regular.012cf6a.woff","012cf6a10129e2275d79d6adac7f3b02"],["fonts/Roboto-Bold.ad140ff.woff","ad140ff02a7091257e2b31619106194e"],["fonts/Roboto-Light.37fbbba.woff","37fbbbad5577a95bdf058307c717c882"],["fonts/Roboto-Medium.303ded6.woff","303ded6436dcf7ea75157e2aeff876ce"],["fonts/Roboto-Regular.081b11e.woff","081b11ebaca8ad30fd092e01451015dc"],["fonts/Roboto-Thin.90d3804.woff","90d3804f0231704c15ccc5861245e8ce"],["index.html","5fe970fb93e9916a60c560c38e85dd96"],["js/app.25e73844dd15b9292e64.js","41bd2f2548c4120e7cba76e0c49fd60c"],["js/chart.176e9b073b2b3b616249.js","a41373d0258cf9685482fc9cc84640c6"],["js/vendor.f626bb62897fe419e26d.js","99bebc0854ce8fca3110a27075294032"],["manifest.json","57fd0577c61cbab917139147544630f9"],["recorder.js","84519b0290515f7278a588b1a156e556"],["statics/avatar.png","d874068d3363cc58727c3ec6f02e408e"],["statics/icons/apple-icon-152x152.png","c56caea1d8eddae2e253c91d41343b32"],["statics/icons/badge-72x72.png","a2f690f6ba09a43c6650c278bbc34b05"],["statics/icons/favicon-16x16.png","7b3b90033253bee4e58049ebbb29d130"],["statics/icons/favicon-32x32.png","31bd481f8544b30c2eeb01bbaae2137b"],["statics/icons/icon-1024x1024.png","3ee9c303a06c2c523d306264e5d81945"],["statics/icons/icon-192x192.png","c43cb48382fddb689ca264e530e56849"],["statics/icons/icon-256x256.png","26d895e1896db4e9ff102a236c24fb39"],["statics/icons/icon-36x36.png","ab1e549b298f3a870c9a00a99cfb031c"],["statics/icons/icon-384x384.png","272ca7fb0669cd86c56b54adea14546e"],["statics/icons/icon-48x48.png","54810a868cca650ba13cb4ab744858f1"],["statics/icons/icon-512x512.png","561d3572056fec8f3a4206d134862499"],["statics/icons/icon-72x72.png","d0d25a0dba185c5e353c871b4cff0408"],["statics/icons/ms-icon-144x144.png","465de5fc8de1a6655a77816ed0b46b68"],["sw-webpush.js","433bda77900f36f4c76eb1bc4a44eff5"]],cacheName="sw-precache-v3-habot-"+(self.registration?self.registration.scope:""),ignoreUrlParametersMatching=[/^utm_/],addDirectoryIndex=function(e,n){var t=new URL(e);return"/"===t.pathname.slice(-1)&&(t.pathname+=n),t.toString()},cleanResponse=function(e){return e.redirected?("body"in e?Promise.resolve(e.body):e.blob()).then(function(n){return new Response(n,{headers:e.headers,status:e.status,statusText:e.statusText})}):Promise.resolve(e)},createCacheKey=function(e,n,t,c){var a=new URL(e);return c&&a.pathname.match(c)||(a.search+=(a.search?"&":"")+encodeURIComponent(n)+"="+encodeURIComponent(t)),a.toString()},isPathWhitelisted=function(e,n){if(0===e.length)return!0;var t=new URL(n).pathname;return e.some(function(e){return t.match(e)})},stripIgnoredUrlParameters=function(e,n){var t=new URL(e);return t.hash="",t.search=t.search.slice(1).split("&").map(function(e){return e.split("=")}).filter(function(e){return n.every(function(n){return!n.test(e[0])})}).map(function(e){return e.join("=")}).join("&"),t.toString()},hashParamName="_sw-precache",urlsToCacheKeys=new Map(precacheConfig.map(function(e){var n=e[0],t=e[1],c=new URL(n,self.location),a=createCacheKey(c,hashParamName,t,!1);return[c.toString(),a]}));function setOfCachedUrls(e){return e.keys().then(function(e){return e.map(function(e){return e.url})}).then(function(e){return new Set(e)})}self.addEventListener("install",function(e){e.waitUntil(caches.open(cacheName).then(function(e){return setOfCachedUrls(e).then(function(n){return Promise.all(Array.from(urlsToCacheKeys.values()).map(function(t){if(!n.has(t)){var c=new Request(t,{credentials:"same-origin"});return fetch(c).then(function(n){if(!n.ok)throw new Error("Request for "+t+" returned a response with status "+n.status);return cleanResponse(n).then(function(n){return e.put(t,n)})})}}))})}).then(function(){return self.skipWaiting()}))}),self.addEventListener("activate",function(e){var n=new Set(urlsToCacheKeys.values());e.waitUntil(caches.open(cacheName).then(function(e){return e.keys().then(function(t){return Promise.all(t.map(function(t){if(!n.has(t.url))return e.delete(t)}))})}).then(function(){return self.clients.claim()}))}),self.addEventListener("fetch",function(e){if("GET"===e.request.method){var n,t=stripIgnoredUrlParameters(e.request.url,ignoreUrlParametersMatching),c="index.html";(n=urlsToCacheKeys.has(t))||(t=addDirectoryIndex(t,c),n=urlsToCacheKeys.has(t));0,n&&e.respondWith(caches.open(cacheName).then(function(e){return e.match(urlsToCacheKeys.get(t)).then(function(e){if(e)return e;throw Error("The cached response that was expected is missing.")})}).catch(function(n){return console.warn('Couldn\'t serve response for "%s" from cache: %O',e.request.url,n),fetch(e.request)}))}}),importScripts("sw-webpush.js");