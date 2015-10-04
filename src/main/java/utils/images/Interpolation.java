/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.images
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.images;

import java.awt.*;

/**
 * @author thomas
 *
 */
public enum Interpolation {

    // {@code RenderingHints.KEY_INTERPOLATION} (e.g.
    // * {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
    // * {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
    // * {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
    // * @param higherQuality

    // KEY(RenderingHints.KEY_INTERPOLATION),
    NEAREST_NEIGHBOR(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR),
    BILINEAR(RenderingHints.VALUE_INTERPOLATION_BILINEAR),
    BICUBIC(RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    private Object hint;

    Interpolation(final Object hint) {
        this.hint = hint;
    }

    public Object getHint() {
        return this.hint;
    }
}
