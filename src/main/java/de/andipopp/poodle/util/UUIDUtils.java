package de.andipopp.poodle.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * Static methods to translate {@link UUID}s 
 * @author Andi Popp
 *
 */
public class UUIDUtils {

	/**
	 * The Base64url encoder
	 */
	public static final Base64.Encoder enc = Base64.getUrlEncoder();
	
	/**
	 * The Base64url decoder
	 */
	public static final Base64.Decoder dec = Base64.getUrlDecoder();
	
	/**
	 * Translate a UUID into a byte array 
	 * @param uuid the UUID to translate
	 * @return byte representation of the UUID
	 */
	public static byte[] uuidToBytes(UUID uuid) {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
		return buffer.array();
	}
	
	/**
	 * Translate a byte array into a UUID.
	 * Only the first 16 bytes of the array will be considered.
	 * @param bytes byte representation of the UUID
	 * @return the resulting UUID object
	 * @throws NotAUuidException if the length of the argument is to short for a UUID (16 bytes)
	 */
	public static UUID bytesToUuid(byte[] bytes) throws NotAUuidException {
		if (bytes.length < 16) throw new NotAUuidException("Need 16 bytes to build a UUID.");
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		return new UUID(buffer.getLong(), buffer.getLong());
	}
	
	/**
	 * Translate a UUID to Base64url.
	 * Since a UUID is always 128 bit, the padding at the end is not necessary
	 * @param uuid the UUID to translate
	 * @param dropPadding if true, the padding "==" at the end is dropped
	 * @return the Base64url representation of the UUID
	 */
	public static String uuidToBase64url(UUID uuid, boolean dropPadding) {
		String base64url = enc.encodeToString(uuidToBytes(uuid));
		if (dropPadding) base64url = base64url.substring(0, base64url.length()-2);
		return base64url;
	}
	
	/**
	 * Version of {@link #uuidToBase64url(UUID, boolean)} which default dropPadding to true
	 * @param uuid the UUID to translate
	 * @return Base64url representation of the UUID without padding
	 */
	public static String uuidToBase64url(UUID uuid) {
		return uuidToBase64url(uuid, true);
	}
	
	/**
	 * Translate a Base64url string into a UUID.
	 * @param base64url the Base64 representation of the UUID
	 * @return the corresponding UUID object
	 * @throws NotAUuidException if the argument is not a 128bit base64url string
	 * 
	 */
	public static UUID base64urlToUuid(String base64url) throws NotAUuidException {
		try {
			return bytesToUuid(dec.decode(base64url));
		} catch (Exception e) {
			throw new NotAUuidException("Not a UUID: "+base64url, e);
		}
	}	
}
