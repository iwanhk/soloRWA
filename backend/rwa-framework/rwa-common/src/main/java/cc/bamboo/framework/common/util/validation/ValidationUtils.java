package cc.bamboo.framework.common.util.validation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * @author 芋道源码
 */
public class ValidationUtils {

    private static final Pattern PATTERN_URL = Pattern
            .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private static final Pattern PATTERN_XML_NCNAME = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");

    public static boolean isMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return false;
        }
        try {
            com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil
                    .getInstance();
            // 默认区域设置为 CN，支持无 + 号的国内号码解析。如果包含国际区号（如 +1），则会按照国际区号解析。
            com.google.i18n.phonenumbers.Phonenumber.PhoneNumber numberProto = phoneUtil.parse(mobile, "CN");
            return phoneUtil.isValidNumber(numberProto);
        } catch (com.google.i18n.phonenumbers.NumberParseException e) {
            return false;
        }
    }

    public static boolean isURL(String url) {
        return StringUtils.hasText(url)
                && PATTERN_URL.matcher(url).matches();
    }

    public static boolean isXmlNCName(String str) {
        return StringUtils.hasText(str)
                && PATTERN_XML_NCNAME.matcher(str).matches();
    }

    public static void validate(Object object, Class<?>... groups) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Assert.notNull(validator);
        validate(validator, object, groups);
    }

    public static void validate(Validator validator, Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (CollUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}
