
#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform float u_blurRadius;

void main() {
    vec4 sum = vec4(0.0);
    vec2 uv = v_texCoord;

    // Horizontal blur
    for (float i = -u_blurRadius; i <= u_blurRadius; i += 1.0) {
        sum += texture2D(u_texture, vec2(uv.x + i / u_resolution.x, uv.y)) / (u_blurRadius * 2.0 + 1.0);
    }

    // Vertical blur
    for (float i = -u_blurRadius; i <= u_blurRadius; i += 1.0) {
        sum += texture2D(u_texture, vec2(uv.x, uv.y + i / u_resolution.y)) / (u_blurRadius * 2.0 + 1.0);
    }

    gl_FragColor = v_color * sum / (2.0 * u_blurRadius + 1.0);
}