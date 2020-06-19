package com.glsx.plat.common.loggin;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.Data;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Data
public class BootTurboFilter extends TurboFilter {

    String marker;
    Marker markerToAccept;

    public BootTurboFilter() {
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        } else {
            return this.markerToAccept.equals(marker) ? FilterReply.ACCEPT : FilterReply.NEUTRAL;
        }
    }

    @Override
    public void start() {
        if (this.marker != null && this.marker.trim().length() > 0) {
            this.markerToAccept = MarkerFactory.getMarker(this.marker);
            super.start();
        }

    }
}
