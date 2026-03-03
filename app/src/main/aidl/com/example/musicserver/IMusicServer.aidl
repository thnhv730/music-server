package com.example.musicserver;

import android.os.Bundle;

interface IMusicServer {
    String getServerVersion();
    Bundle getServerInfo();
}