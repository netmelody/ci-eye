package org.netmelody.cieye.server.configuration.avatar;

/**
 * @author Dmitry Sidorenko
 */
public interface PictureUrlProvider {
  String imageUrlFor(String image);

  String handlesPrefix();
}
