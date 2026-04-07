package cc.bamboo.framework.common.validation;

import cn.hutool.core.util.StrUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@org.springframework.stereotype.Component
public class MobileValidator implements ConstraintValidator<Mobile, String> {

    @Value("${rwa.mobile.allowed-codes:all}")
    private String allowedCodes;

    @Value("${rwa.mobile.excluded-codes:}")
    private String excludedCodes;

    @Override
    public void initialize(Mobile annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果手机号为空，默认不校验，即校验通过
        if (StrUtil.isEmpty(value)) {
            return true;
        }

        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil
                    .getInstance();
            // 默认区域设置为 CN，支持无 + 号的国内号码解析。如果包含国际区号（如 +1），则会按照国际区号解析。
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(value, "CN");

            // 1. 校验格式是否正确
            if (!phoneUtil.isValidNumber(numberProto)) {
                return false;
            }

            // 2. 校验国家/地区码是否在允许列表中
            int countryCode = numberProto.getCountryCode();

            if (StrUtil.isNotEmpty(excludedCodes)) {
                boolean isExcluded = java.util.Arrays.stream(excludedCodes.split(","))
                        .map(s -> s.replace("+", "").trim())
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .anyMatch(code -> code == countryCode);

                if (isExcluded) {
                    // 自定义错误信息
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("暂不支持 +" + countryCode + " 地区的手机号")
                            .addConstraintViolation();
                    return false;
                }
            }

            if (!"all".equalsIgnoreCase(allowedCodes)) {

                boolean isAllowed = java.util.Arrays.stream(allowedCodes.split(","))
                        .map(s -> s.replace("+", "").trim())
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .anyMatch(code -> code == countryCode);

                if (!isAllowed) {
                    // 自定义错误信息
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("目前仅支持 " + allowedCodes + " 地区的手机号")
                            .addConstraintViolation();
                    return false;
                }
            }

            return true;

        } catch (com.google.i18n.phonenumbers.NumberParseException e) {
            return false;
        }
    }

}
