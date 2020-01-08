package com.jobxhub.rpc.netty.idle.domain;

import io.netty.handler.timeout.IdleStateEvent;

import java.util.Date;
import java.util.Objects;

/**
 * @author Autorun
 */
public class IdleRequest {

	private String remoteAddr;

	private IdleStateEvent event;

	private String side;

	private Date time;

	public IdleRequest setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
		return this;
	}

	public IdleRequest setEvent(IdleStateEvent event) {
		this.event = event;
		return this;
	}

	public IdleRequest setSide(String side) {
		this.side = side;
		return this;
	}

	public IdleRequest setTime(Date time) {
		this.time = time;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		IdleRequest that = (IdleRequest) o;
		return Objects.equals(remoteAddr, that.remoteAddr) &&
				Objects.equals(event, that.event) &&
				Objects.equals(side, that.side) &&
				Objects.equals(time, that.time);
	}

	@Override
	public int hashCode() {
		return Objects.hash(remoteAddr, event, side, time);
	}

	@Override
	public String toString() {
		return "IdleRequest{" +
				"remoteAddr='" + remoteAddr + '\'' +
				", event=" + event +
				", side='" + side + '\'' +
				", time=" + time +
				'}';
	}
}
